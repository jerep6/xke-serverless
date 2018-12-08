const books = [
  {
    id: 1,
    title: "The Shining",
    author_id: 1,
    year: 1977,
    likes: 12000
  },
  {
    id: 2,
    title: "Black House",
    author_id: 1,
    year: 2001,
    likes: 8700
  },
  {
    id: 3,
    title: "Da Vinci Code",
    author_id: 2,
    year: 2003,
    likes: 130000,
  },
  {
    id: 4,
    title: "Angels & Demons",
    author_id: 2,
    year: 2000,
    likes: 98000,
  },
  {
    id: 5,
    title: "Deception Point",
    author_id: 2,
    year: 2001,
    likes: 56000,
  },
  {
    id: 6,
    title: "Les Fourmis",
    author_id: 3,
    year: 1991,
    likes: 46800,
  },
  {
    id: 7,
    title: "Le Jour des fourmis",
    author_id: 3,
    year: 1992,
    likes: 48300,
  },
  {
    id: 8,
    title: "La Révolution des fourmis",
    author_id: 3,
    year: 1996,
    likes: 53000,
  }
];

export async function list () {
  return books;
};

export async function get(bookId) {
  return books.filter(elt => elt.id == bookId)[0];
};