variable "aws_region" {
  default = "ap-northeast-2"
}

variable "allowed_ip_1" {
  description = "허용할 SSH IP 1 (예: 1.2.3.4/32)"
  type        = string
}

variable "allowed_ip_2" {
  type = string
}

variable "allowed_ip_3" {
  type = string
}