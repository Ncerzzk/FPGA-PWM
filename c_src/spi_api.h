#ifndef __SPI_API_H
#define __SPI_API_H

#include <stdio.h>
#include <stdint.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <linux/spi/spidev.h>

#define SPI_SPEED_HZ 5000000         // SPI 时钟频率为 5MHz

void spi_write_datas(int fd, uint8_t addr, uint16_t * data_array,uint8_t len);
void spi_write(int fd, uint8_t addr, uint16_t value);
uint16_t spi_read(int fd,uint8_t addr);

#endif