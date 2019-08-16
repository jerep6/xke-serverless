from flask import (
    jsonify,
    request
)
from books import books_repository


def register_routes(app):
    @app.route('/books', methods=['GET'])
    def get_databases():
        result = books_repository.list_books()
        return jsonify(result)

    @app.route('/books/<string:book_id>', methods=['GET'])
    def get_database(book_id: str):
        result = books_repository.get(book_id)
        return jsonify(result)

    @app.route('/books', methods=['POST'])
    def save_documentation():
        payload = request.json
        print("Request", payload)
        return jsonify(payload), 201

