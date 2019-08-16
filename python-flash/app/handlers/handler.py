import awsgi
from flask_server import create_app


def lambda_handler(event, context):
    print("Lambda event", event)
    return awsgi.response(create_app(), event, context)
