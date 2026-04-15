package com.wefly.wefly;

import com.wefly.wefly.model.*;
import com.wefly.wefly.repository.AnnouncementRepository;
import com.wefly.wefly.repository.BookingRepository;
import com.wefly.wefly.repository.FlightRepository;
import com.wefly.wefly.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class WeflyApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeflyApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(
            AnnouncementRepository annRepo,
            UserRepository userRepo,
            FlightRepository flightRepo,
            BookingRepository bookingRepo) {

        return args -> {
            // 1. Crear Usuario
            User fran = User.builder().name("Fran").email("fran@wefly.com").isVerified(true).build();
            userRepo.save(fran);

            // 2. CREAR A MILA (Será el ID 2)
            User mila = User.builder()
                    .name("Mila")
                    .email("mila@wefly.com")
                    .isVerified(false)
                    .build();
            userRepo.save(mila);

            // 2. Crear Vuelo
            Flight flight = new Flight();
            flight.setFlightNumber("IB3110");
            flight.setOrigin("ALC");
            flight.setDestination("MAD");
            flight.setArrivalTime(LocalDateTime.now().plusHours(2));
            flightRepo.save(flight);

            // 3. Crear Reserva CONFIRMADA (El pase de seguridad)
            Booking booking = new Booking();
            booking.setUser(fran);
            booking.setFlight(flight);
            booking.setReservationCode("ABCDEF");
            booking.setConfirmed(true);
            bookingRepo.save(booking);

            // 4. Crear Anuncio
            Announcement ann = new Announcement();
            ann.setTitle("Compartir Taxi");
            ann.setDescription("Busco gente para el centro");
            ann.setFlight(flight);
            ann.setAuthor(fran);
            ann.setCategory(Announcement.Category.TO_AIRPORT);
            ann.setType(AnnouncementType.TRANSPORT);
            ann.setSeatsAvailable(3);
            annRepo.save(ann);

            System.out.println("✅ Entorno de prueba listo con reserva confirmada para IB3110");
        };
    }
}