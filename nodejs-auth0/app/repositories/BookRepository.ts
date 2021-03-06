import { Book } from '../../typings/Book';
import * as uuid from 'uuid/v4';

const books = [
  {
    id: "aaa",
    title: "The Shining",
    author_id: "1",
    year: 1977,
    likes: 12000
  },
  {
    id: "bbb",
    title: "Black House",
    author_id: "1",
    year: 2001,
    likes: 8700
  },
  {
    id: "ccc",
    title: "Da Vinci Code",
    author_id: "2",
    year: 2003,
    likes: 130000,
  },
  {
    id: "ddd",
    title: "Angels & Demons",
    author_id: "2",
    year: 2000,
    likes: 98000,
  },
  {
    id: "eee",
    title: "Deception Point",
    author_id: "2",
    year: 2001,
    likes: 56000,
  },
  {
    id: "fff",
    title: "Les Fourmis",
    author_id: "3",
    year: 1991,
    likes: 46800,
  },
  {
    id: "ggg",
    title: "Le Jour des fourmis",
    author_id: "3",
    year: 1992,
    likes: 48300,
  },
  {
    id: "hhh",
    title: "La Révolution des fourmis",
    author_id: "3",
    year: 1996,
    likes: 53000,
  }
];

export async function list (): Promise<Book[]> {
  return books;
};

export async function get(bookId: string): Promise<Book> {
  return books.filter(elt => elt.id == bookId)[0];
};

export async function create(book: Book): Promise<Book> {
  const bookToCreate = {...{id: uuid() }, ...book};
  books.push(bookToCreate);
  return bookToCreate;
};
