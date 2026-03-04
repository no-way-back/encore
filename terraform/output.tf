output "vpc_id" {
  value = aws_vpc.main.id
}

output "internal_alb_dns" {
  value = aws_lb.internal.dns_name
}

output "private_subnet_ids" {
  value = [
    aws_subnet.private1.id,
    aws_subnet.private2.id,
    aws_subnet.private3.id,
    aws_subnet.private4.id,
  ]
}