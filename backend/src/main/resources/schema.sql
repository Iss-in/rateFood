CREATE TABLE IF NOT EXISTS restaurent (
                                          id SERIAL PRIMARY KEY,
                                          name VARCHAR(255),
                                          location VARCHAR(255),
                                          upvote INT,
                                          downvote INT,
                                          rating FLOAT,
                                          city FLOAT
);

CREATE TABLE IF NOT EXISTS  dish (
                                     id SERIAL PRIMARY KEY,
                                     name VARCHAR(255),
                                     cuisine VARCHAR(255),
                                     place VARCHAR(255),
                                     upvote INT,
                                     downvote INT,
                                     rating FLOAT,
                                     restaurent_id INT NOT NULL,
                                     FOREIGN KEY (restaurent_id) REFERENCES restaurent(id)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE
);