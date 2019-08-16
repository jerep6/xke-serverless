import os

from flask import Flask
from flask_cors import CORS
from exceptions import exception_handling
from exceptions.simple_exceptions import SimpleException, ErrorCode
import routes


def create_app():
    print("STARTING SIMPLE APPLICATION...")

    app = Flask(__name__, instance_relative_config=True)
    CORS(app, expose_headers=['Content-Type', 'Authorization'])

    _init_config(app)

    # Exception handling
    exception_handling.register_exception_handlers(app)

    # Routes
    routes.register_routes(app)

    return app


def _init_config(app):
    app.config.from_mapping(
        ENVIRONMENT=os.environ.get('ENVIRONMENT'),
    )

    verify_mandatory_vars_or_fail(app.config, [
        'ENVIRONMENT'
    ])

    print('Config:', app.config)


def verify_mandatory_vars_or_fail(config, env_vars: list):
    for env_var in env_vars:
        if env_var not in config:
            raise SimpleException(error_code=ErrorCode.INITIALISATION_ERROR, message=f'Env variable {env_var} is mandatory')
