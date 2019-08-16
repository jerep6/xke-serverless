from flask import jsonify
import json
from os import path


def register_routes(app):

    @app.route('/')
    def index():
        return 'SIMPLE REST API'

    @app.route('/health', methods=['GET'])
    def health():
        file_path = path.join(path.dirname(__file__), '..', 'info.json')
        with open(file_path, 'r') as file:
            health_info = {'info': json.loads(file.read())}
            return jsonify({'status': 'UP', **health_info})

