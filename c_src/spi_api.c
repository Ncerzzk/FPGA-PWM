#include "spi_api.h"
#include <stdlib.h>

void spi_write_datas(int fd, uint8_t addr, uint16_t * data_array,uint8_t len){
    uint8_t tx[len*2+1];

    tx[0] = addr<<1 | 1;
    for(int i=0;i<len;++i){
        tx[1+i*2] = data_array[i] >>8;
        tx[2+i*2] = data_array[i] & 0xff;
    }

    struct spi_ioc_transfer transfer = {
        .tx_buf = (unsigned long)tx,
        .rx_buf = 0,
        .len = sizeof(tx),
        .speed_hz = SPI_SPEED_HZ,
        .bits_per_word = 8,
        .delay_usecs = 0,
    }; 
    if (ioctl(fd, SPI_IOC_MESSAGE(1), &transfer) < 0) {
        perror("SPI_IOC_MESSAGE");
        exit(EXIT_FAILURE);
    }
}

void spi_write(int fd, uint8_t addr, uint16_t value){
    uint8_t tx[3]={addr<<1 | 1};
    tx[1]=value>>8;
    tx[2]=value&0xFF;

    struct spi_ioc_transfer transfer = {
        .tx_buf = (unsigned long)tx,
        .rx_buf = 0,
        .len = sizeof(tx),
        .speed_hz = SPI_SPEED_HZ,
        .bits_per_word = 8,
        .delay_usecs = 0,
    }; 
    if (ioctl(fd, SPI_IOC_MESSAGE(1), &transfer) < 0) {
        perror("SPI_IOC_MESSAGE");
        exit(EXIT_FAILURE);
    }
}

uint16_t spi_read(int fd,uint8_t addr){
    uint8_t tx[3]={addr<<1 | 0}; 
    uint8_t rx[3]={0};
    uint16_t ret=0; 
    struct spi_ioc_transfer transfer = {
        .tx_buf = (unsigned long)tx,
        .rx_buf = (unsigned long)rx,
        .len = sizeof(tx),
        .speed_hz = SPI_SPEED_HZ,
        .bits_per_word = 8,
        .delay_usecs = 0,
    }; 
    if (ioctl(fd, SPI_IOC_MESSAGE(1), &transfer) < 0) {
        perror("SPI_IOC_MESSAGE");
        exit(EXIT_FAILURE);
    } 

    ret = rx[1] << 8 | rx[2];
    return ret;
}