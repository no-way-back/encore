# ========== S3 Gateway Endpoint ==========
# Gateway 타입은 보안그룹 없이, 라우팅 테이블에 route 자동 추가
resource "aws_vpc_endpoint" "s3" {
  vpc_id            = aws_vpc.main.id
  service_name      = "com.amazonaws.${var.aws_region}.s3"
  vpc_endpoint_type = "Gateway"

  route_table_ids = [
    aws_route_table.private1.id,
    aws_route_table.private2.id,
    aws_route_table.private3.id,
    aws_route_table.private4.id,
  ]
  tags = { Name = "encore-vpce-s3" }
}

# ========== ECR API Interface Endpoint ==========
resource "aws_vpc_endpoint" "ecr_api" {
  vpc_id              = aws_vpc.main.id
  service_name        = "com.amazonaws.${var.aws_region}.ecr.api"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true

  subnet_ids         = [aws_subnet.private1.id, aws_subnet.private2.id]
  security_group_ids = [aws_security_group.ecs_tasks.id]
  tags               = { Name = "encore-vpce-ecr-api" }
}

# ========== ECR DKR Interface Endpoint ==========
resource "aws_vpc_endpoint" "ecr_dkr" {
  vpc_id              = aws_vpc.main.id
  service_name        = "com.amazonaws.${var.aws_region}.ecr.dkr"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true

  subnet_ids         = [aws_subnet.private1.id, aws_subnet.private2.id]
  security_group_ids = [aws_security_group.ecs_tasks.id]
  tags               = { Name = "encore-vpce-ecr-dkr" }
}

# ========== CloudWatch Logs Interface Endpoint ==========
resource "aws_vpc_endpoint" "logs" {
  vpc_id              = aws_vpc.main.id
  service_name        = "com.amazonaws.${var.aws_region}.logs"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true

  subnet_ids         = [aws_subnet.private1.id, aws_subnet.private2.id]
  security_group_ids = [aws_security_group.ecs_tasks.id]
  tags               = { Name = "encore-vpce-logs" }
}