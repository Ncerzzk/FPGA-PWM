#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <unistd.h>
#include "spi_api.h"
#include <string.h>
#include <getopt.h>
#include <assert.h>

// #define SPI_DEVICE "/dev/spidev0.2"  // SPI 设备文件路径

static struct option long_options[] = {
    {"help", 0, NULL, 'h'},
    {"mode", 1, NULL, 'm'},
    {"dshot",0,NULL,'c'},
    {"crc",0,NULL,'c'},
    {0, 0, 0, 0}
};

void print_usage(){
    printf("usage :\n");
    printf("--mode n, choose spi mode: 0~3\n");
    printf("--dshot(crc), use dshot mode, which would add crc value in low 4 byte\n");
}

int main(int argc, char *argv[])
{
    int SPI_MODE = 0;
    char SPI_DEVICE[20] = {0};
    uint8_t dshot_mode = 0;
    int opt;
    while ((opt = getopt_long(argc, argv, "m:", long_options, NULL)) != -1)
    {
        switch (opt)
        {
        case 'h':
            print_usage();
            return 0;
        case 'm':
            SPI_MODE = atoi(optarg); 
            break;
        case 'c':
            dshot_mode = 1;
            break;
        default:
            printf("Unknown option %c\n", opt);
            break;
        }
    }

    strcpy(SPI_DEVICE, argv[optind++]);
    int fd;
    fd = open(SPI_DEVICE, O_RDWR); // 打开 SPI 设备
    if (fd < 0)
    {
        perror("open");
        exit(EXIT_FAILURE);
    }

    // 设置 SPI 模式
    if (ioctl(fd, SPI_IOC_WR_MODE, &SPI_MODE) < 0)
    {
        perror("SPI_IOC_WR_MODE");
        exit(EXIT_FAILURE);
    }


    uint16_t target_addr = (uint16_t) strtoul(argv[optind++],0,0);
    if(optind < argc){
        uint16_t val =  (uint16_t) strtoul(argv[optind++],0,0);
        if(dshot_mode){
            assert(val < 2048); // dshot value should less then 11 bits
            val <<= 1; // bit 12
            uint16_t crc = (val ^ (val >> 4) ^ (val >> 8)) & 0x0F;
            val = val << 4 | crc;
        }
        printf("write to addr:%#x val:%#x\n", target_addr, val);
        spi_write(fd, target_addr,val);
    }
    printf("read addr:%#x val:%#x\n",target_addr,spi_read(fd, target_addr));

    close(fd); // 关闭 SPI 设备

    return 0;
}
