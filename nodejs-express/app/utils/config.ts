
export default {
  logs : {
    json: process.env.LOG_FORMAT === 'json'
  },
  dynamodb: {
    book: process.env.TABLE_BOOK || 'books'
  }
}
