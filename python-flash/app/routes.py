from health import health_controller
from books import books_controller


def register_routes(app):
    health_controller.register_routes(app)
    books_controller.register_routes(app)
