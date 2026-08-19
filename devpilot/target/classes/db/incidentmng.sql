CREATE TABLE users (

    id INT PRIMARY KEY AUTO_INCREMENT,

    name VARCHAR(100) NOT NULL,

    email VARCHAR(100) UNIQUE NOT NULL,

    role VARCHAR(50) NOT NULL,
     
    employment_status VARCHAR(20) NOT NULL DEFAULT 'CURRENTLY_WORKING'
);



CREATE TABLE incidents (

    id INT PRIMARY KEY AUTO_INCREMENT,

    title VARCHAR(200) NOT NULL,

    description TEXT,

    severity VARCHAR(50) NOT NULL,

    status VARCHAR(50) NOT NULL,

    reported_by_id INT NOT NULL,

    assigned_to_id INT,

    created_at DATETIME NOT NULL
    DEFAULT CURRENT_TIMESTAMP,

    updated_at DATETIME NOT NULL
    DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,


    FOREIGN KEY (reported_by_id)
    REFERENCES users(id),

    FOREIGN KEY (assigned_to_id)
    REFERENCES users(id)

);



CREATE TABLE incident_events (

    id INT PRIMARY KEY AUTO_INCREMENT,

    incident_id INT NOT NULL,

    event_type VARCHAR(50) NOT NULL,

    message TEXT NOT NULL,

    created_by_id INT NOT NULL,

    created_at DATETIME NOT NULL
    DEFAULT CURRENT_TIMESTAMP,


    FOREIGN KEY (incident_id)
    REFERENCES incidents(id)
    ON DELETE CASCADE,


    FOREIGN KEY (created_by_id)
    REFERENCES users(id)

);
