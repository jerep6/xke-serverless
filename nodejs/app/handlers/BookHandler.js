const repo = require('../repositories/BookRepository');

module.exports.list = function (event, context, callback) {
  console.log('Event', JSON.stringify(event));

  const books = repo.list();

  callback(null, {
    statusCode: 200,
    body: JSON.stringify(books)
  });
};

module.exports.get = function (event, context, callback) {
  console.log('Event', JSON.stringify(event));

  const books = repo.get(event.pathParameters.bookId);

  callback(null, {
    statusCode: 200,
    body: JSON.stringify(books)
  });
};