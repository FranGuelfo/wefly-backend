package com.wefly.wefly;

import com.wefly.wefly.model.Announcement;
import com.wefly.wefly.model.Booking;
import com.wefly.wefly.model.Flight;
import com.wefly.wefly.model.User;
import com.wefly.wefly.repository.AnnouncementRepository;
import com.wefly.wefly.repository.BookingRepository;
import com.wefly.wefly.repository.FlightRepository;
import com.wefly.wefly.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class WeflyApplication {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(WeflyApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(
            AnnouncementRepository annRepo,
            UserRepository userRepo,
            FlightRepository flightRepo,
            BookingRepository bookingRepo,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // 1. Usuarios (Mantenemos igual)
            User fran = User.builder()
                    .name("Fran")
                    .email("fran@wefly.com")
                    .isVerified(true)
                    .password(passwordEncoder.encode("123456"))
                    .build();
            userRepo.save(fran);
            User mila = User.builder().name("Mila").email("mila@wefly.com").isVerified(false).build();
            userRepo.save(mila);

            // 2. Vuelo (Mantenemos igual)
            Flight flight = new Flight();
            flight.setFlightNumber("IB3240");
            flight.setOrigin("MAD");
            flight.setDestination("CDG");
            flight.setPlazas(3);
            flightRepo.save(flight);

            // 3. Reservas (Mantenemos igual)
            Booking bookingFran = new Booking();
            bookingFran.setUser(fran);
            bookingFran.setFlight(flight);
            bookingFran.setReservationCode("ABCDEF");
            bookingFran.setIsConfirmed(true);
            bookingRepo.save(bookingFran);

            // 4. Anuncio inicial REFACTORIZADO
            Announcement annInicial = getAnnouncement(fran);

            annRepo.save(annInicial);

            System.out.println("✅ Entorno de prueba listo (v2) con modelo actualizado");
        };
    }

    private static @NonNull Announcement getAnnouncement(User fran) {
        Announcement annInicial = new Announcement();
        annInicial.setTitle("Taxi compartido al centro"); // Nuevo campo
        annInicial.setDescription("Voy directo a la Torre Eiffel, si alguien se apunta para dividir gastos, bienvenido."); // Nuevo campo
        annInicial.setSeatsAvailable(3);
        annInicial.setCategory("TO_AIRPORT");
        annInicial.setOrigin("Aeropuerto París (CDG)");
        annInicial.setDestination("Hotel Centro París / Torre Eiffel");
        annInicial.setDateStr("28 Mayo, 2026");
        annInicial.setFlightNumber("IB3240");

        // Mapeo a los nuevos campos de autor
        annInicial.setAuthorId(fran.getId());
        annInicial.setAuthorName(fran.getName());
        annInicial.setAuthorPhone("+34600000000"); // Valor de prueba
        return annInicial;
    }
}