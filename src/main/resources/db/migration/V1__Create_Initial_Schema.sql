--- GUESTS
CREATE TABLE guests (
    id CHAR(36) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    document VARCHAR(30) NOT NULL,
    email VARCHAR(120) NOT NULL,
    phone VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_guests PRIMARY KEY (id),
    CONSTRAINT uq_guests_document UNIQUE (document),
    CONSTRAINT uq_guests_email UNIQUE (email)
);

--- ROOMS
CREATE TABLE rooms (
    id CHAR(36) NOT NULL,
    number INT NOT NULL,
    type VARCHAR(20) NOT NULL, -- STANDARD | DELUXE | SUITE
    capacity INT NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL, -- ATIVO | INATIVO
    CONSTRAINT pk_rooms PRIMARY KEY (id),
    CONSTRAINT uq_rooms_number UNIQUE (number)
);

--- RESERVATIONS
CREATE TABLE reservations (
    id CHAR(36) NOT NULL,
    guest_id CHAR(36) NOT NULL,
    room_id CHAR(36) NOT NULL,
    checkin_expected DATE NOT NULL,
    checkout_expected DATE NOT NULL,
    checkin_at TIMESTAMP,
    checkout_at TIMESTAMP,
    status VARCHAR(20) NOT NULL, -- CREATED | CHECKED_IN | CHECKED_OUT | CANCELED
    estimated_amount DECIMAL(10,2),
    final_amount DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT pk_reservations PRIMARY KEY (id),
    CONSTRAINT fk_reservations_guest FOREIGN KEY (guest_id) REFERENCES guests(id),
    CONSTRAINT fk_reservations_room FOREIGN KEY (room_id) REFERENCES rooms(id)
);

--- Índices úteis
CREATE INDEX idx_rooms_status ON rooms (status);
CREATE INDEX idx_reservations_room ON reservations (room_id);
CREATE INDEX idx_reservations_status ON reservations (status);
CREATE INDEX idx_reservations_date_range ON reservations (checkin_expected, checkout_expected);

--- SEED (exemplos)
INSERT INTO guests (id, full_name, document, email, phone)
VALUES
('11111111-1111-1111-1111-111111111111', 'Ana Silva', '12345678901', 'ana@example.com', '+55-11-99999-1111'),
('22222222-2222-2222-2222-222222222222', 'Bruno Souza', '98765432101', 'bruno@example.com', '+55-21-98888-2222');

INSERT INTO rooms (id, number, type, capacity, price_per_night, status)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 101, 'STANDARD', 2, 250.00, 'ATIVO'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 201, 'DELUXE', 3, 380.00, 'ATIVO'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 301, 'SUITE', 4, 520.00, 'ATIVO');

INSERT INTO reservations (
    id, guest_id, room_id, checkin_expected, checkout_expected, status, estimated_amount, created_at
)
VALUES
('99999999-9999-9999-9999-999999999999',
'11111111-1111-1111-1111-111111111111',
'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
DATE '2025-11-05', DATE '2025-11-07', 'CREATED', 2 * 250.00,
CURRENT_TIMESTAMP
);