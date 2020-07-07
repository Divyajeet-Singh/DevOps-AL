// Insert your config and certificates for kubernetes cluster (Dockerfile)
// Create PV and Service scripts first and then only run Deployment Script
// Create a seed job for creating all the jobs
// Make sure you have connectivity with Docker_Host
// Assuming developer will also keep Dockerfile in Github
// Don't forget to disable 'In-Process Script Approval'
// Install Build Pipeline Plugin
// Configure Docker Cloud on jenkins
// Configure email on jenkins
// Create repo with master and task4-dev branch


// Build Pipeline
buildPipelineView('Task-6') {
    filterBuildQueue()
    filterExecutors()
    title('Task 6 Pipeline')
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
        shell ('''cd ./Task-6/
            check_php=$(ls | grep .php$ | wc -l)
            check_html=$( ls | grep .html$ | wc -l)
            if [ $check_php -ge 1 ]
            then
            #sudo docker login
            sudo docker build -t divyajeetsingh/php:${BUILD_NUMBER} .
            sudo docker push
            elif [ $check_html -ge 1 ]
            then
            #sudo docker login
            sudo docker build -t divyajeetsingh/html:${BUILD_NUMBER} .
            sudo docker push
            else
            echo "Language currently not supported"
            exit 1
            fi''')
    }
}

// Job 2 (Trigger based on successful completion of Job 1)
freeStyleJob('Job2') {
    label ('kubectl')
    scm {
        github('Divyajeet-Singh/DevOps-AL', 'master')
    }
    triggers {
      upstream('Job1', 'SUCCESS')
    }
    steps {
        shell ('''export my_kubernetes_cluster_ip=192.168.76.190
            cd ./Task-6/
            check_php=$(ls | grep .php$ | wc -l)
            check_html=$( ls | grep .html$ | wc -l)
            check_php_pod=$(kubectl get pod -l app=php,environment=dev | wc -l)
            check_html_pod=$(kubectl get pod -l app=html,environment=dev | wc -l)
            if [ $check_php -ge 1 ]
            then
                if [ $check_php_pod = 0 ]
                then
                kubectl apply -f ./Php_PVC.yaml ./Php_Service.yaml ./Php_Deployment.yaml
                echo "Now Visit http://${my_kubernetes_cluster_ip}:32500"
                else
                echo "Existing Deployment"
                kubectl rollout restart -f ./Php_Deployment.yaml
                echo "Now Visit http://${my_kubernetes_cluster_ip}:32500"
                fi
            elif [ $check_html -ge 1 ]
            then
                if [ $check_html_pod = 0 ]
                then
                kubectl apply -f ./Html_PVC.yaml ./Html_Service.yaml ./Html_Deployment.yaml
                echo "Now Visit http://${my_kubernetes_cluster_ip}:32600"
                else
                echo "Existing Deployment"
                kubectl rollout restart -f ./Html_Deployment.yaml
                echo "Now Visit http://${my_kubernetes_cluster_ip}:32600"
                fi
            else
            echo "Language currently not supported"
            exit 1
            fi''')
    }
}

freeStyleJob('Job3') {
    scm {
        github('Divyajeet-Singh/DevOps-AL', 'master')
    }
    triggers {
      upstream('Job2', 'SUCCESS')
    }
    steps {
        shell ('''export my_kubernetes_cluster_ip=192.168.76.190
            cd ./Task-6/
            check_php=$(ls | grep .php$ | wc -l)
            check_html=$( ls | grep .html$ | wc -l)
            check_php_pod=$(kubectl get pod -l app=php,environment=dev | wc -l)
            check_html_pod=$(kubectl get pod -l app=html,environment=dev | wc -l)
            if [ $check_html = 0 ]
            then
                if [ $check_php_pod = 0 ]
                then
                exit
                else
                test=$(curl -i http://${my_kubernetes_cluster_ip}:32500| grep HTTP | awk '{print $2}')
                    if [ $test = 200 ]
                    exit
                    else
                    exit 1
                    fi
                fi
            elif [ $check_php = 0 ]
            then
                if [ $check_html_pod = 0 ]
                then
                exit
                else
                test=$(curl -i http://${my_kubernetes_cluster_ip}:32600 | grep HTTP | awk '{print $2}')
                    if [ $test = 200 ]
                    exit
                    else
                    exit 1
                    fi
                fi
            else
            echo "Testing for language currently not supported"
            exit 1
            fi''')
    }
    publishers {
        extendedEmail {
            triggers {
                failure {
                attachBuildLog(true)
                subject('Application is having trouble to run')
                content('Latest code require bug fixing, kindly look at the attached log, fix the bug and push it to Github for re-deployment')
                sendTo {
                    developers()
                    }
                }
            }
        }
    }
}