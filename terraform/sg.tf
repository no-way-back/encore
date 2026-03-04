# ========== Bastion SG ==========
resource "aws_security_group" "bastion" {
  name        = "encore-bastion-sg"
  description = "external request to encore private subnet"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.allowed_ip_1]
  }
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.allowed_ip_2]
  }
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.allowed_ip_3]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = { Name = "encore-bastion-sg" }
}

# ========== External ALB SG ==========
resource "aws_security_group" "external_alb" {
  name        = "encore-external-alb-sg"
  description = "encore-external-alb"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = { Name = "encore-external-alb-sg" }
}

# ========== ECS Gateway SG ==========
resource "aws_security_group" "ecs_gateway" {
  name        = "encore-ecs-gateway-sg"
  description = "external alb to ecs application gateway"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.external_alb.id]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = { Name = "encore-ecs-gateway-sg" }
}

# ========== Internal ALB SG ==========
resource "aws_security_group" "internal_alb" {
  name        = "encore-internal-alb-sg"
  description = "gateway to internal alb"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_gateway.id]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = { Name = "encore-internal-alb-sg" }
}

# ========== ECS Tasks SG ==========
resource "aws_security_group" "ecs_tasks" {
  name        = "encore-ecs-tasks-sg"
  description = "encore service all ecs group(without-gateway)"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "user service from internal-alb"
    from_port       = 18081
    to_port         = 18081
    protocol        = "tcp"
    security_groups = [aws_security_group.internal_alb.id]
  }
  ingress {
    description     = "funding service from internal-alb"
    from_port       = 18082
    to_port         = 18082
    protocol        = "tcp"
    security_groups = [aws_security_group.internal_alb.id]
  }
  ingress {
    description     = "project service from internal-alb"
    from_port       = 18083
    to_port         = 18083
    protocol        = "tcp"
    security_groups = [aws_security_group.internal_alb.id]
  }
  ingress {
    description     = "payment service from internal-alb"
    from_port       = 18084
    to_port         = 18084
    protocol        = "tcp"
    security_groups = [aws_security_group.internal_alb.id]
  }
  ingress {
    description     = "reward service from internal-alb"
    from_port       = 18085
    to_port         = 18085
    protocol        = "tcp"
    security_groups = [aws_security_group.internal_alb.id]
  }
  ingress {
    description     = "bastion direct access (test)"
    from_port       = 18082
    to_port         = 18082
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion.id]
  }
  ingress {
    description = "self - VPC Endpoint HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    self        = true
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = { Name = "encore-ecs-tasks-sg" }
}

# ========== RDS SG ==========
resource "aws_security_group" "rds" {
  name        = "encore-rds-sg"
  description = "bastion and ecs to rds"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    security_groups = [aws_security_group.bastion.id]
  }
  ingress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    security_groups = [aws_security_group.ecs_tasks.id]
  }
  tags = { Name = "encore-rds-sg" }
}

# ========== ElastiCache SG ==========
resource "aws_security_group" "elasticache" {
  name        = "encore-elasticache-sg"
  description = "ecs tasks to elasticache"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "write"
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_tasks.id]
  }
  ingress {
    description     = "read"
    from_port       = 6380
    to_port         = 6380
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_tasks.id]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = { Name = "encore-elasticache-sg" }
}

# ========== MSK SG ==========
resource "aws_security_group" "msk" {
  name        = "encore-msk-sg"
  description = "ecs tasks to msk"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "ecs tasks to msk (SASL/IAM)"
    from_port       = 9098
    to_port         = 9098
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_tasks.id]
  }
  ingress {
    description     = "bastion to msk"
    from_port       = 9098
    to_port         = 9098
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion.id]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = { Name = "encore-msk-sg" }
}