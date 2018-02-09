const repo = require('../repositories/BookRepository');

module.exports.list = function (event, context, callback) {

  console.log('Event', JSON.stringify(event));

  const books = repo.list();

  callback(null, {
    statusCode: 200,
    body: JSON.stringify(books)
  });
};