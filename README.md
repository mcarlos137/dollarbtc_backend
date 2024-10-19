# DollarBTC Backend

The DollaBTC Backend project provides services to support the business model of the following projects:

1. DollarBTC Crypto Trading
2. DollarBTC OTC
3. MoneyClick Wallet
4. KaiKai Wallet & Social Services


## Create Docker Container:

Follow the steps below to create a Docker Container to run the project and test it.


### Step 1: Clone Git Project

    git clone https://github.com/mcarlos137/dollarbtc_backend.git


### Step 2: Move to main folder

    cd dollarbtc_backend


### Step 3: Create Docker Volume & import base files

Project repository provides at docker/volumes/dollarbtc_backend.tar.gz all files to run the project for testing purposes.

    cd docker/volumes && mkdir MAIN && tar -xf dollarbtc_backend.tar.gz -C MAIN && docker volume create dollarbtc_backend && docker run -it -d --name main -v dollarbtc_backend:/MAIN alpine:latest && docker cp MAIN main:/ && docker container stop main && docker container rm main && docker image rm alpine && cd ../..
     

### Step 4: Create Docker Image

Using jib-maven-plugin to create a clean image to serve at 8080/tcp port

    cd dollarbtc_cryptocurrency_exchange_service-jar && mvn compile com.google.cloud.tools:jib-maven-plugin:2.3.0:dockerBuild -Djib.container.ports=8080/tcp -Dimage=dollarbtc_backend && cd ..


### Step 5: Create & Run Docker Container

    



