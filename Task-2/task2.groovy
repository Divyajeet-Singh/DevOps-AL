// Don't forget to disable 'In-Process Script Approval'
// Install Build Pipeline Plugin
// Configure Docker Cloud on jenkins
// Configure email on jenkins
// Create repo with master and task2-dev branch

// Build Pipeline
buildPipelineView('Task-2') {
    filterBuildQueue()
    filterExecutors()
    title('Task 2 Pipeline')
    displayedBuilds(5)
    selectedJob('Job1')
    alwaysAllowManualTrigger()
    showPipelineParameters()
    refreshFrequency(60)
}

// Job 1 (Triggered based on git commit)
freeStyleJob('Job1') {
    scm {
        github('Divyajeet-Singh/DevOps-AL', 'master')
    }
    triggers {
      scm('H/5 * * * *')
    }
    steps {
        shell ('echo "Success !!"')
    }
}

// Job 2 (Trigger based on successful completion of Job 1)
freeStyleJob('Job2') {
    scm {
        github('Divyajeet-Singh/DevOps-AL', 'master')
    }
    triggers {
      upstream('Job1', 'SUCCESS')
    }
    steps {
        shell ('''export my_container_host=192.168.76.190
            cd ./Task-2/
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
            exit 1
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

// Job3 (Testing of deployed container)
freeStyleJob('Job3') {
    scm {
        github('Divyajeet-Singh/DevOps-AL', 'master')
    }
    triggers {
      upstream('Job2', 'SUCCESS')
    }
    steps {
        shell ('''export my_container_host=192.168.76.190
            cd ./Task-2/
            check_php=$(ls | grep .php$ | wc -l)
            check_html=$( ls | grep .html$ | wc -l)
            check_php_container=$(sudo docker ps -a | grep php | grep dev | wc -l)
            check_html_container=$(sudo docker ps -a | grep html | grep dev | wc -l)
            if [ $check_php -ge 1 ]
            then
                if [ $check_php_container = 0 ]
                then
                exit
                else
                test=$(curl -i http://${my_container_host}:8500 | grep HTTP | awk '{print $2}')
                    if [ $test = 200 ]
                    exit
                    else
                    exit 1
                    fi
                fi
            elif [ $check_html -ge 1 ]
            then
                if [ $check_html_container = 0 ]
                then
                exit
                else
                test=$(curl -i http://${my_container_host}:8600 | grep HTTP | awk '{print $2}')
                    if [ $test = 200 ]
                    exit
                    else
                    exit 1
                    fi
                fi
            else
            echo "Testing for language currently not supported"
            fi''')
    }
}

// Job4 (Send email on run failure - This can be achieved in previous step but doing it here to show job chaining)
freeStyleJob('Job4') {
    scm {
        github('Divyajeet-Singh/DevOps-AL', 'master')
    }
    triggers {
      upstream('Job3', 'UNSTABLE')
    }
    steps {
        shell ('''export my_container_host=192.168.76.190
            cd ./Task-2/
            check_php=$(ls | grep .php$ | wc -l)
            check_html=$( ls | grep .html$ | wc -l)
            check_php_container=$(sudo docker ps -a | grep php | grep dev | wc -l)
            check_html_container=$(sudo docker ps -a | grep html | grep dev | wc -l)
            if [ $check_php -ge 1 ]
            then
                if [ $check_php_container = 0 ]
                then
                exit
                else
                test=$(curl -i http://${my_container_host}:8500 | grep HTTP | awk '{print $2}')
                    if [ $test = 200 ]
                    exit
                    else
                    exit 1
                    fi
                fi
            elif [ $check_html -ge 1 ]
            then
                if [ $check_html_container = 0 ]
                then
                exit
                else
                test=$(curl -i http://${my_container_host}:8600 | grep HTTP | awk '{print $2}')
                    if [ $test = 200 ]
                    exit
                    else
                    exit 1
                    fi
                fi
            else
            echo "Testin for language currently not supported"
            fi''')
    }
    publishers {
        extendedEmail {
            triggers {
                always {
                attachBuildLog(true)
                subject('Application is having trouble to run')
                content('Latest code require bug fixing')
                sendTo {
                    developers()
                    }
                }
            }
        }
    }
}

// Job5 (Monitoring Job)
freeStyleJob('Job5') {
    scm {
        github('Divyajeet-Singh/DevOps-AL', 'master')
    }
    triggers {
      cron('* * * * *')
    }
    steps {
        shell ('''export my_container_host=192.168.76.190
            cd ./Task-2/
            check_php=$(ls | grep .php$ | wc -l)
            check_html=$( ls | grep .html$ | wc -l)
            check_php_container=$(sudo docker ps -a | grep php | grep dev | wc -l)
            check_html_container=$(sudo docker ps -a | grep html | grep dev | wc -l)
            if [ $check_php -ge 1 ]
            then
                if [ $check_php_container = 0 ]
                then
                exit
                else
                test=$(curl -i http://${my_container_host}:8500 | grep HTTP | awk '{print $2}')
                    if [ $test = 200 ]
                    exit
                    else
                    sudo docker rm -f dev-php-container
                    sudo docker run -d -p 8500:80 --name dev-php-container php
                    sudo docker exec dev-php-container rm -rf /usr/src/myapp
                    sudo docker cp `pwd`/ dev-php-container:/usr/src/myapp
                    fi
                fi
            elif [ $check_html -ge 1 ]
            then
                if [ $check_html_container = 0 ]
                then
                exit
                else
                test=$(curl -i http://${my_container_host}:8600 | grep HTTP | awk '{print $2}')
                    if [ $test = 200 ]
                    exit
                    else
                    sudo docker rm -f dev-html-container
                    sudo docker run -d -p 8600:80 --name dev-html-container httpd
                    sudo docker exec dev-html-container rm -rf /usr/local/apache2/htdocs/
                    sudo docker cp `pwd`/ dev-html-container:/usr/local/apache2/htdocs/
                    fi
                fi
            else
            echo "Testin for language currently not supported"
            fi''')
    }
}
