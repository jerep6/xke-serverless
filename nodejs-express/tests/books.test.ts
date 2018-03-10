import 'jest';
import * as supertest from 'supertest';
import app from '../app/index';
import { afterAllHook, beforeAllHook, afterEachHook, populateDatabase } from './tests.utils'


describe('Test the root path', () => {
  beforeAll(beforeAllHook, 10000);
  afterAll(afterAllHook);
  afterEach(afterEachHook);

  test('It should response the GET method', async () => {
    await populateDatabase('./data/books-01.json');

    supertest(app)
      .get('/tests/books')
      .expect('Content-Type', 'application/json')
      .expect(200)
      .end((err, response) => {
        expect(response.body).toEqual([{
          "id": "id1",
          "title": "Da Vinci Code",
          "description": "Description Da Vinci Code",
          "author_id": "2",
          "year": 2003,
          "likes": 130000
        },
          {
            "id": "id2",
            "title": "Angels & Demons",
            "author_id": "2",
            "year": 2000,
            "likes": 98000
          }]);
      });
  });
});
