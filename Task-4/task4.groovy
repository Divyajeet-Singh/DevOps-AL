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
buildPipelineView('Task-4') {
    filterBuildQueue()
    filterExecutors()
    title('Task 4 Pipeline')
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
        shell ('''cd ./Task-4/
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
            sudo docker build -t divyajeetsingh/html:${BUILD_NUMBER}
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
            cd ./Task-4/
            check_php=$(ls | grep .php$ | wc -l)
            check_html=$( ls | grep .html$ | wc -l)
            check_php_pod=$(kubectl get pod -l app=php,environment=dev | wc -l)
            check_html_pod=$(kubectl get pod -l app=html,environment=dev | wc -l)
            if [ $check_php -ge 1 ]
            then
                if [ $check_php_pod = 0 ]
                then
                kubectl create deployment php-deployment --image=divyajeetsingh/php:latest
                kubectl label deployment php-deployment --overwrite=true app=php environment=dev
                kubectl expose deployment php-deployment --port=80 --target-port=80 --type=NodePort --labels app=php,environment=dev,type=svc
                port=$(kubectl get svc --selector app=php,environment=dev |  grep TCP | awk '{print $5}' | awk '{split($0,a,":"); print a[2]}' | awk '{split($0,a,"/"); print a[1]}')
                echo "Now Visit http://${my_kubernetes_cluster_ip}:$port"
                else
                echo "Existing Deployment"
                kubectl rollout restart deployment php-deployment
                port=$(kubectl get svc --selector app=php,environment=dev |  grep TCP | awk '{print $5}' | awk '{split($0,a,":"); print a[2]}' | awk '{split($0,a,"/"); print a[1]}')
                echo "Now Visit http://${my_kubernetes_cluster_ip}:$port"
                fi
            elif [ $check_html -ge 1 ]
            then
                if [ $check_html_container = 0 ]
                then
                kubectl create deployment html-deployment --image=divyajeetsingh/html:latest
                kubectl label deployment html-deployment --overwrite=true app=html environment=dev
                kubectl expose deployment html-deployment --port=80 --target-port=80 --type=NodePort --labels app=html,environment=dev,type=svc
                port=$(kubectl get svc --selector app=html,environment=dev |  grep TCP | awk '{print $5}' | awk '{split($0,a,":"); print a[2]}' | awk '{split($0,a,"/"); print a[1]}')
                echo "Now Visit http://${my_kubernetes_cluster_ip}:$port"
                else
                echo "Existing Deployment"
                kubectl rollout restart deployment html-deployment
                port=$(kubectl get svc --selector app=html,environment=dev |  grep TCP | awk '{print $5}' | awk '{split($0,a,":"); print a[2]}' | awk '{split($0,a,"/"); print a[1]}')
                echo "Now Visit http://${my_kubernetes_cluster_ip}:$port"
                fi
            else
            echo "Language currently not supported"
            exit 1
            fi''')
    }
}
