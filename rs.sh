#!/usr/bin/env bash

mongo mongodb://mongo:27017 <<EOF
var config = {
    _id : "rs0",
    members: [
        {
            "_id": 0,
            "host": "mongo:27017",
            "priority": 4
        },
        {
            "_id": 1,
            "host": "mongo_1:27017",
            "priority": 2
        },
        {
            "_id": 2,
            "host": "mongo_2:27017",
            "priority": 1
        }
    ]
}
rs.initiate(config);
EOF