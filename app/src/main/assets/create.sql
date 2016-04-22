/* v3 - for reference only */

CREATE TABLE users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  email TEXT UNIQUE NOT NULL,
  password TEXT NOT NULL,
  name TEXT,
  phone TEXT
);

CREATE TABLE user_favorites (
  user_id INTEGER,
  apartment_id INTEGER,
  PRIMARY KEY (user_id, apartment_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (apartment_id) REFERENCES apartments(id)
);

CREATE TABLE search_history (
  _id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER,
  min_cost INTEGER DEFAULT NULL,
  max_cost INTEGER DEFAULT NULL,
  has_gym BOOLEAN DEFAULT NULL,
  has_parking BOOLEAN DEFAULT NULL,
  min_beds INTEGER DEFAULT NULL,
  max_beds INTEGER DEFAULT NULL,
  min_bathrooms INTEGER DEFAULT NULL,
  max_bathrooms INTEGER DEFAULT NULL,
  min_area INTEGER DEFAULT NULL,
  max_area INTEGER DEFAULT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE apartments (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  address TEXT,
  zip INTEGER,
  bedrooms INTEGER,
  bathrooms INTEGER,
  square_feet NUMERIC,
  rent NUMERIC,
  owner_id INTEGER,
  FOREIGN KEY (owner_id) REFERENCES owners(id)
);

CREATE TABLE owners (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  complex_name TEXT,
  owner_phone TEXT NULLABLE,
  owner_email TEXT NULLABLE
);

CREATE TABLE amenities (
  apartment_id INTEGER PRIMARY KEY,
  parking BOOLEAN,
  gym BOOLEAN,
  gas NUMERIC,
  electricity NUMERIC,
  internet NUMERIC,
  cable NUMERIC,
  thermostat NUMERIC,
  FOREIGN KEY (apartment_id) REFERENCES apartments(id)
);


CREATE TABLE web_apartments (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT,
  address TEXT,
  rent NUMERIC,
  latitude NUMERIC,
  longitude NUMERIC,
  owner_email TEXT NULLABLE,
  owner_phone TEXT NULLABLE,
  owner_website TEXT NULLABLE
);