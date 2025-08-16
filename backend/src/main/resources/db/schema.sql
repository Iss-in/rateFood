CREATE TABLE IF NOT EXISTS restaurant (
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
                      restaurant_id INT NOT NULL,
                      FOREIGN KEY (restaurant_id) REFERENCES restaurant(id)
                          ON DELETE CASCADE
                          ON UPDATE CASCADE
);