FROM centos
RUN dnf update -y && dnf install -y java-1.8.0-openjdk wget
RUN wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
RUN rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key
RUN dnf update -y && dnf install -y jenkins
CMD ["java","-jar","/usr/lib/jenkins/jenkins.war"]
EXPOSE 8080
