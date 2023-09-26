CC = gcc
CFLAGS = -Wall -g

spi_pwm: spi_api.o spi_pwm.o
	$(CC) $(CFLAGS) -o spi_pwm spi_api.o spi_pwm.o

spi_api.o: spi_api.c
	$(CC) $(CFLAGS) -c spi_api.c

spi_pwm.o: spi_api.c spi_pwm.c
	$(CC) $(CFLAGS) -c spi_pwm.c

clean:
	rm -f spi_api.o spi_pwm.o spi_pwm