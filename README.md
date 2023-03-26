# FPGA-PWM Module

This module is designed for generating pulse-width modulation (PWM) signals on an FPGA platform. It supports both SPI and I2C communication protocols and provides up to 8 channels of PWM output. Additionally, it has 2 sub-counters that can generate signals with different frequencies.

## Features

- Supports SPI and I2C communication protocols
- Up to 8 channels of PWM output
- 2 sub-counters that can generate signals with different frequencies
- Supports a maximum SPI communication speed of 20MHz
- Supports a maximum I2C communication speed of 400kHz
- Uses 397 LUTs and 447 FFs in Gowin FPGA GW1N-LV1

## Registers

| Offset   | Name                 | Description                                                  |
| -------- | --------------------| -------------------------------------------------------------|
| 0x00     | subpwm0_period      | Period of sub-counter 0                                      |
| 0x01     | CCR0                | Compare value of channel 0                                   |
| 0x02     | CCR1                | Compare value of channel 1                                   |
| 0x03     | CCR2                | Compare value of channel 2                                   |
| 0x04     | CCR3                | Compare value of channel 3                                   |
| 0x05     | CCR4                | Compare value of channel 4                                   |
| 0x06     | CCR5                | Compare value of channel 5                                   |
| 0x07     | CCR6                | Compare value of channel 6                                   |
| 0x08     | CCR7                | Compare value of channel 7                                   |
| 0x20     | pwm_channel_map0    | PWM channel mapping register for channel 0 to 8              |
| 0x40     | subpwm1_period      | Period of sub-counter 1                                      |
| 0x50     | config              | Configuration register. Bit 15: enable timeout; Bit 14: enable sub-counter 1; Low 5 bits: pre-divider |
| 0x7D     | timeout_max_low     | Timeout value (low 16 bits)                                  |
| 0x7E     | timeout_max_high    | Timeout value (high 16 bits)                                 |
| 0x7F     | watchdog            | Watchdog register. Toggle the LSB to clear the timeout flag  |

## License

This module is licensed under the GPL 3.0 license. Please see the [LICENSE](LICENSE) file for details.

## Authors

- [ncerzzk](https://github.com/ncerzzk) - Initial work

## Acknowledgments

- Thanks to [OpenAI](https://openai.com/) for providing the ChatGPT model used to generate this documentation.


# FPGA PWM模块

该FPGA PWM模块可以在FPGA上实现PWM输出。模块支持SPI/I2C协议，最多支持8个通道以及2个子计数器（可以生成2种不同频率）。模块已经在Gowin FPGA GW1N-LV1上进行过测试，消耗资源为397个LUT和447个FF。

## 寄存器映射

| 偏移地址 | 名称 | 描述                                        |
| --- | --- |-------------------------------------------|
| 0x0 | subpwm0_period | 子计数器0的周期                                  |
| 0x1 | CCR0 | 通道0的比较值                                   |
| 0x2 | CCR1 | 通道1的比较值                                   |
| 0x3 | CCR2 | 通道2的比较值                                   |
| 0x4 | CCR3 | 通道3的比较值                                   |
| 0x5 | CCR4 | 通道4的比较值                                   |
| 0x6 | CCR5 | 通道5的比较值                                   |
| 0x7 | CCR6 | 通道6的比较值                                   |
| 0x8 | CCR7 | 通道7的比较值                                   |
| 0x20 | pwm_channel_map0 | 通道0到7的PWM通道映射寄存器                          |
| 0x40 | subpwm1_period | 子计数器1的周期                                  |
| 0x50 | config | 配置寄存器，Bit 15:使能超时；Bit 14:使能子计数器1；低5位：预分频器 |
| 0x7f | watchdog | 看门狗寄存器，切换LSB以清除超时标志                       |
| 0x7e | timeout max high | 超时值高16位                                   |
| 0x7d | timeout max low | 超时值低16位                                   |

## 版权信息

该FPGA PWM模块是基于GPL 3.0许可证发布的，您可以自由使用、修改和分发该模块。

