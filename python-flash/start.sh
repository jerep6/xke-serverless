#!/bin/bash

export FLASK_APP="app/flask_server.py"
export PYTHONPATH="app/"

ENVIRONMENT=local FLASK_DEBUG=0 flask run
