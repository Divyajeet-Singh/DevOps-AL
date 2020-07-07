// Don't forget to disable 'In-Process Script Approval'
// Configure Docker Cloud on jenkins
// Configure email on jenkins
// Create repo with master and task1-dev branch

// Approval Page
freeStyleJob('Approval-Page') {
    scm {
        github('Divyajeet-Singh/DevOps-AL', 'master')
    }
    triggers {
      scm('H/5 * * * *')
    }
    steps {
        shell ('''export my_container_host=192.168.76.190
            check_container=$(sudo docker ps -a | grep approval | wc -l)
            cd ./Approval-Page/Task-1/
            if [ $check_container = 0 ]
            then
                sudo docker run -d -p 50000:80 --name approval-container httpd
                sudo docker exec approval-container rm -rf /usr/local/apache2/htdocs/
                sudo docker cp `pwd`/ approval-container:/usr/local/apache2/htdocs/
                echo "Now Visit http://${my_container_host}:50000"
            else
                echo "Container already running, new content available at http://${my_container_host}:50000"
                sudo docker exec approval-container rm -rf /usr/local/apache2/htdocs/
                sudo docker cp `pwd`/ approval-container:/usr/local/apache2/htdocs/
            fi''')
    }
    publishers {
        extendedEmail {
            triggers {
                success {
                attachBuildLog(true)
                subject('DApproval Page - Task 1')
                content('View approval Page at:- http://192.168.76.190:50000')
                sendTo {
                    recipientList('div89.receive@gmail.com')
                    }
                }
            }
        }
    }
}

// Development Job
freeStyleJob('Devlopment-Deployment') {
    scm {
        github('Divyajeet-Singh/DevOps-AL', 'task1-dev')
    }
    triggers {
      scm('H/5 * * * *')
    }
    steps {
        shell ('''export my_container_host=192.168.76.190
            cd ./Task-1/
            check_php=$(ls | grep .php$ | wc -l)
            check_html=$( ls | grep .html$ | wc -l)
            check_php_container=$(sudo docker ps -a | grep php | grep dev | wc -l)
            check_html_container=$(sudo docker ps -a | grep html | grep dev | wc -l)
            if [ $check_php -ge 1 ]
            then
                if [ $check_php_container = 0 ]
                then
                sudo docker run -d -p 8500:80 --name dev-php-container php
                sudo docker exec dev-php-container rm -rf /usr/src/myapp
                sudo docker cp `pwd`/ dev-php-container:/usr/src/myapp
                echo "Now Visit http://${my_container_host}:8500"
                else
                echo "Container already running, new content available at http://${my_container_host}:8500"
                sudo docker exec dev-php-container rm -rf /usr/src/myapp
                sudo docker cp `pwd`/ dev-php-container:/usr/src/myapp
                fi
            elif [ $check_html -ge 1 ]
            then
                if [ $check_html_container = 0 ]
                then
                sudo docker run -d -p 8600:80 --name dev-html-container httpd
                sudo docker exec dev-html-container rm -rf /usr/local/apache2/htdocs/
                sudo docker cp `pwd`/ dev-html-container:/usr/local/apache2/htdocs/
                echo "Now Visit http://${my_container_host}:8600"
                else
                echo "Container already running, new content available at http://${my_container_host}:8600"
                sudo docker exec dev-html-container rm -rf /usr/local/apache2/htdocs/
                sudo docker cp `pwd`/ dev-html-container:/usr/local/apache2/htdocs/
                fi
            else
            echo "Language currently not supported"
            fi''')
    }
    publishers {
        extendedEmail {
            triggers {
                success {
                attachBuildLog(true)
                subject('Deployed the development code')
                content('''Congratulation updated code has been deployed to devlopment server
                Verification at:- http://192.168.76.190:8500 or http://192.168.76.190:8600
                Approval at:- http://192.168.76.190:50000''')
                sendTo {
                    recipientList('div89.receive@gmail.com')
                    }
                }
            }
        }
    }
}

// Development Approved Job
// You must have SSH login into Github for this step to be successful (or your credentials must be cached or stored in windows credentials manager)
freeStyleJob('Development-Approved') {
    scm {
        github('Divyajeet-Singh/DevOps-AL', 'master')
    }
    configure { project ->
        (project / 'authToken').setValue('approved')
	}
    steps {
        shell('''echo "Build Approve"
                git fetch origin
                git checkout task1-dev
                git merge origin/task1-dev
                git checkout master
                git merge origin/master
                git merge -X theirs task1-dev
                git push origin master''')
	}
    publishers {
	extendedEmail {
		triggers {
			always {
				attachBuildLog(true)
				subject('Development-Build -- Approved')
				content('Congratulation your efforts paid off and we are deploying your build into Production')
				sendTo {
					recipientList('div89.receive@gmail.com')
					}
				}
			}
		}
	}
}

// Development Rejected Job

freeStyleJob('Development-Rejected') {
    configure { project ->
        (project / 'authToken').setValue('rejected')
	}
    steps {
        shell('echo "Build Rejected"')
	}
    publishers {
	extendedEmail {
		triggers {
			always {
				attachBuildLog(true)
				subject('Development-Build -- Rejected')
				content('Kindly re-work on the build it currently not ready for Production')
				sendTo {
					recipientList('div89.receive@gmail.com')
					}
				}
			}
		}
	}
}

// Production Job
freeStyleJob('Production-Deployment') {
    scm {
        github('Divyajeet-Singh/DevOps-AL', 'master')
    }
    triggers {
      scm('H/5 * * * *')
    }
    steps {
        shell ('''export my_container_host=192.168.76.190
            cd ./Task-1/
            check_php=$(ls | grep .php$ | wc -l)
            check_html=$( ls | grep .html$ | wc -l)
            check_php_container=$(sudo docker ps -a | grep php | grep prod | wc -l)
            check_html_container=$(sudo docker ps -a | grep html | grep prod | wc -l)
            if [ $check_php -ge 1 ]
            then
                if [ $check_php_container = 0 ]
                then
                sudo docker run -d -p 85:80 --name prod-php-container php
                sudo docker exec prod-php-container rm -rf /usr/src/myapp
                sudo docker cp `pwd`/ prod-php-container/usr/src/myapp
                echo "Now Visit http://${my_container_host}:85"
                else
                echo "Container already running, new content available at http://${my_container_host}:85"
                sudo docker exec prod-php-container rm -rf /usr/src/myapp
                sudo docker cp `pwd`/ prod-php-container:/usr/src/myapp
                fi
            elif [ $check_html -ge 1 ]
            then
                if [ $check_html_container = 0 ]
                then
                sudo docker run -d -p 86:80 --name prod-html-container httpd
                sudo docker exec prod-html-container rm -rf /usr/local/apache2/htdocs/
                sudo docker cp `pwd`/ prod-html-container:/usr/local/apache2/htdocs/
                echo "Now Visit http://${my_container_host}:86"
                else
                echo "Container already running, new content available at http://${my_container_host}:86"
                sudo docker exec prod-html-container rm -rf /usr/local/apache2/htdocs/
                sudo docker cp `pwd`/ prod-html-container:/usr/local/apache2/htdocs/
                fi
            else
            echo "Language currently not supported"
            fi''')
    }
    publishers {
        extendedEmail {
            triggers {
                success {
                attachBuildLog(true)
                subject('Deployed the production code')
                content('Congratulation updated code has been deployed to production')
                sendTo {
                    recipientList('div89.receive@gmail.com')
                    }
                }
            }
        }
    }
}
