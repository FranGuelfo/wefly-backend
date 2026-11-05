-- Primero los usuarios
INSERT INTO users (name, email) VALUES ('Fran', 'fran@wefly.com');
INSERT INTO users (name, email) VALUES ('Pasajero X', 'test@wefly.com');

-- Luego los anuncios (asociándolos al user_id 1 o 2)
INSERT INTO announcements (flight_number, title, description, category, type, seats_available, contact_info, user_id, created_at)
VALUES ('IB3402', 'Taxi al centro', 'Comparto taxi', 'FROM_AIRPORT', 'TRANSPORT', 2, '@fran', 1, CURRENT_TIMESTAMP);