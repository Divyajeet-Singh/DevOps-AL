FROM centos
RUN dnf update -y && dnf install -y java-1.8.0-openjdk wget curl
RUN echo 'root:jenkins' | chpasswd
RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl
RUN chmod +x ./kubectl && mv ./kubectl /usr/local/bin
COPY ./ca.crt ./client.crt ./client.key ./config /.kube/
ENV KUBECONFIG="/.kube/config"
RUN chown -hR root:root /.kube/
RUN wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
RUN rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key
RUN dnf update -y && dnf install -y jenkins
CMD ["java","-jar","/usr/lib/jenkins/jenkins.war"]
EXPOSE 8080