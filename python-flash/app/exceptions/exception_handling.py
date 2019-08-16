from flask import (make_response, jsonify)

from exceptions.simple_exceptions import SimpleException


def register_exception_handlers(app):
    @app.errorhandler(404)
    def not_found(error):
        return make_response(jsonify({'message': 'Not found'}), 404)

    @app.errorhandler(SimpleException)
    def special_exception_handler(error: SimpleException):
        status_code = error.error_code.value.get("status_code")
        return make_response(jsonify(error.format()), status_code)

    @app.errorhandler(Exception)
    def special_exception_handler(error):
        print(str(error))
        return make_response(jsonify({'message': 'Internal Server Error'}), 500)
