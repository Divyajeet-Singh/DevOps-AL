# DevOps-AL

❗️TASK-1❗️

JOB#1
If Developer push to dev branch then Jenkins will fetch from dev and deploy on dev-docker environment.

JOB#2
If Developer push to master branch then Jenkins will fetch from master and deploy on master-docker environment.
both dev-docker and master-docker environment are on different docker containers.

JOB#3
Manually the QA team will check (test) for the website running in dev-docker environment. If it is running fine then Jenkins will merge the dev branch to master branch and trigger #job 2

❗️TASK-2❗️

1. Create container image that’s has Jenkins installed  using dockerfile 

2. When we launch this image, it should automatically starts Jenkins service in the container.

3. Create a job chain of job1, job2, job3 and  job4 using build pipeline plugin in Jenkins 

4.  Job1 : Pull  the Github repo automatically when some developers push repo to Github.

5.  Job2 : By looking at the code or program file, Jenkins should automatically start the respective language interpreter installed image container to deploy code ( eg. If code is of  PHP, then Jenkins should start the container that has PHP already installed ).

6. Job3 : Test your app if it  is working or not.

7. Job4 : if app is not working , then send email to developer with error messages.

8. Create One extra job job5 for monitor : If container where app is running. fails due to any reson then this job should automatically start the container again.

❗️Task-3❗️

Perform second task on top of Kubernetes where we use Kubernetes resources like Pods, ReplicaSet, Deployment, PVC and Service.

1. Create container image that’s has Jenkins installed  using dockerfile  Or You can use the Jenkins Server on RHEL 8/7
2.  When we launch this image, it should automatically starts Jenkins service in the container.
3.  Create a job chain of job1, job2, job3 and  job4 using build pipeline plugin in Jenkins 
4.  Job1 : Pull  the Github repo automatically when some developers push repo to Github.
5. Job2 : 
    1. By looking at the code or program file, Jenkins should automatically start the respective language interpreter installed image container to deploy code on top of Kubernetes ( eg. If code is of  PHP, then Jenkins should start the container that has PHP already installed )
    2.  Expose your pod so that testing team could perform the testing on the pod
    3. Make the data to remain persistent ( If server collects some data like logs, other user information )
6.  Job3 : Test your app if it  is working or not.
7.  Job4 : if app is not working , then send email to developer with error messages and redeploy the application after code is being edited by the developer

❗️Task-4❗️

Create A dynamic Jenkins cluster and perform task-3 using the dynamic Jenkins cluster.
Steps to proceed as:

1.  Create container image that’s has Linux  and other basic configuration required to run Slave for Jenkins. ( example here we require kubectl to be configured )
2. When we launch the job it should automatically starts job on slave based on the label provided for dynamic approach.
3. Create a job chain of job1 & job2 using build pipeline plugin in Jenkins 
4.  Job1 : Pull  the Github repo automatically when some developers push repo to Github and perform the following operations as:
    1.  Create the new image dynamically for the application and copy the application code into that corresponding docker image
    2.  Push that image to the docker hub (Public repository) 
 ( Github code contain the application code and Dockerfile to create a new image )
5. Job2 ( Should be run on the dynamic slave of Jenkins configured with Kubernetes kubectl command): Launch the application on the top of Kubernetes cluster performing following operations:
    1.  If launching first time then create a deployment of the pod using the image created in the previous job. Else if deployment already exists then do rollout of the existing pod making zero downtime  for the user.
    2. If Application created first time, then Expose the application. Else don’t expose it.

❗️Task-5❗️

Integrate Prometheus and Grafana and perform in following way:
1.  Deploy them as pods on top of Kubernetes by creating resources Deployment, ReplicaSet, Pods or Services
2.  And make their data to be remain persistent 
3.  And both of them should be exposed to outside world

❗️Task-6❗️

Perform third task with the help of Jenkins coding file ( called as jenkinsfile approach ) and perform the with following phases:

1. Create container image that’s has Jenkins installed  using dockerfile  Or You can use the Jenkins Server on RHEL 8/7
2.  When we launch this image, it should automatically starts Jenkins service in the container.
3.  Create a job chain of job1, job2, job3 and  job4 using build pipeline plugin in Jenkins 
4.  Job2 ( Seed Job ) : Pull  the Github repo automatically when some developers push repo to Github.
5. Further on jobs should be pipeline using written code  using Groovy language by the developer
6. Job1 :  
    1. By looking at the code or program file, Jenkins should automatically start the respective language interpreter installed image container to deploy code on top of Kubernetes ( eg. If code is of  PHP, then Jenkins should start the container that has PHP already installed )
    2.  Expose your pod so that testing team could perform the testing on the pod
    3. Make the data to remain persistent using PVC ( If server collects some data like logs, other user information )
7.  Job3 : Test your app if it  is working or not.
8.  Job4 : if app is not working , then send email to developer with error messages and redeploy the application after code is being edited by the developer
