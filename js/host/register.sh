#!/bin/bash

cd $(dirname $0)

mkdir -p /Library/Google/Chrome/NativeMessagingHosts
cp com.croc.external_app.json /Library/Google/Chrome/NativeMessagingHosts
