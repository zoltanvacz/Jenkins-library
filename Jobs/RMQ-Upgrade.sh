#!/bin/bash

# Check if all feature flags are enabled, if not enable them
enable_disabled_flags() {
    disabled_flags=$(sudo rabbitmqctl list_feature_flags | awk '$2 == "disabled" {print $1}')
    echo "$disabled_flags"
    if [ -z "$disabled_flags" ]; then
        echo "No disabled flags to enable."
    else
        echo "Enabling disabled flags..."
        sudo rabbitmqctl enable_feature_flag all
    fi
}

enable_disabled_flags
sudo rabbitmqctl list_feature_flags

#Upgrade RMQ
sudo apt-mark unhold erlang-*
sudo apt update
sudo apt install --only-upgrade -y erlang-*=1:26.2.2-1
sudo apt install --only-upgrade -y rabbitmq-server=3.12.2-1

sudo service rabbitmq-server restart

EXPECTED_VERSION="3.12.2"
MAX_TRIES=6
TRIES=0

while true; do
    VERSION=$(sudo service rabbitmq-server status | grep -oP "RabbitMQ version: \K([0-9]+\.[0-9]+\.[0-9]+)")
    echo "Version: $VERSION"
    if [[ "$VERSION" == "$EXPECTED_VERSION" ]] 
    then
        echo "Version is correct"
        break
    else
        echo "Version is not the expected. Restarting service..."
        sudo service rabbitmq-server restart
        (TRIES++)

        if [ $TRIES -ge $MAX_TRIES ]; then
            echo "Maximum tries reached. Exiting..."
            break
        fi

        echo "Sleeping for 10 seconds before checking again..."
        sleep 10
    fi
done

#sudo service rabbitmq-server restart
sudo service rabbitmq-server status | grep -E "Uptime \(seconds\): [0-9]+" && echo "Running" || echo "Not Running"
#sudo service rabbitmq-server status | grep -E "RabbitMQ version: 3.12.2" && echo "Version is correct" || echo "Version is not the expected"

enable_disabled_flags
sudo rabbitmqctl list_feature_flags