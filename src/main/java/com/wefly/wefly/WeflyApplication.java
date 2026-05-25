package com.wefly.wefly;

import com.wefly.wefly.model.Announcement;
import com.wefly.wefly.model.AnnouncementType;
import com.wefly.wefly.model.Booking;
import com.wefly.wefly.model.Flight;
import com.wefly.wefly.model.User;
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
            // 1. Crear Usuario Fran (Admin/Autor)
            User fran = User.builder()
                    .name("Fran")
                    .email("fran@wefly.com")
                    .isVerified(true)
                    .bio("Creador de WeFly. Viajero incansable y dev de corazón.")
                    .profilePictureUrl("https://i.pravatar.cc/150?u=fran")
                    .build();
            userRepo.save(fran);

            // 2. CREAR A MILA (ID 2 - Nuestra usuaria de pruebas)
            User mila = User.builder()
                    .name("Mila")
                    .email("mila@wefly.com")
                    .isVerified(false)
                    .bio("Me encanta compartir taxi y conocer gente nueva en los viajes. ¡Nos vemos en la terminal!")
                    .profilePictureUrl("https://i.pravatar.cc/150?u=mila")
                    .build();
            userRepo.save(mila);

            // 3. Crear Vuelo de prueba
            Flight flight = new Flight();
            flight.setFlightNumber("IB3110");
            flight.setOrigin("ALC");
            flight.setDestination("MAD");
            flight.setArrivalTime(LocalDateTime.now().plusHours(2));
            flightRepo.save(flight);

            // 4. Crear Reserva CONFIRMADA para Fran
            // (Esto permite que Fran pueda publicar anuncios en este vuelo)
            Booking booking = new Booking();
            booking.setUser(fran);
            booking.setFlight(flight);
            booking.setReservationCode("ABCDEF");
            booking.setIsConfirmed(true);
            bookingRepo.save(booking);

            // 5. Crear Anuncio inicial
            Announcement ann = new Announcement();
            ann.setTitle("Compartir Taxi al Centro");
            ann.setDescription("Tengo un taxi reservado para 4 personas. Salimos 15 min después del aterrizaje.");
            ann.setFlight(flight);
            ann.setAuthor(fran);
            ann.setCategory(Announcement.Category.TO_AIRPORT);
            ann.setType(AnnouncementType.TRANSPORT);
            ann.setSeatsAvailable(3);
            annRepo.save(ann);

            // Crear Reserva CONFIRMADA para Mila (Añade esto)
            Booking bookingMila = new Booking();
            bookingMila.setUser(mila); // Asociamos a Mila (ID 2)
            bookingMila.setFlight(flight); // Al mismo vuelo IB3110
            bookingMila.setReservationCode("MILA12");
            bookingMila.setIsConfirmed(true); // Imprescindible para que pase el filtro
            bookingRepo.save(bookingMila);

            System.out.println("-------------------------------------------------");
            System.out.println("✅ Entorno de prueba listo:");
            System.out.println("   - Usuario 1: Fran (Con Bio y Foto)");
            System.out.println("   - Usuario 2: Mila (Con Bio y Foto)");
            System.out.println("   - Vuelo: IB3110 activo");
            System.out.println("   - Base de datos: H2 activa en puerto 8081");
            System.out.println("-------------------------------------------------");
        };
    }
}