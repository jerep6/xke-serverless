import 'jest';
import * as supertest from 'supertest';
import app from '../app/index';

describe('Test the root path', () => {
  test('It should response the GET method', async () => {

    supertest(app)
      .get('/tests/books')
      .expect('Content-Type', 'application/json')
      .expect(200)
      .end((err, response) => {
        expect(response.body.map(e => e.id)).toEqual(expect.arrayContaining(['aaa', 'bbb', 'ccc', 'ddd', 'eee', 'fff', 'hhh']));
      });
  });
});
