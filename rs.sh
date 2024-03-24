#!/bin/bash

sleep 10

mongo --eval "rs.initiate({'_id': 'rs0', 'members': [{'_id': 0, 'host': 'mongo_primary:27017'}, {'_id': 1, 'host': 'mongo_secondary_1:27017'}, {'_id': 2, 'host': 'mongo_secondary_2:27017'}]})"
