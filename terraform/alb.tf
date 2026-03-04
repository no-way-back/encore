# ========== Internal ALB ==========
resource "aws_lb" "internal" {
  name               = "encore-internal-alb"
  internal           = true
  load_balancer_type = "application"
  security_groups    = [aws_security_group.internal_alb.id]
  subnets            = [aws_subnet.private1.id, aws_subnet.private2.id]
  tags               = { Name = "encore-internal-alb" }
}

# ========== Target Group: funding ==========
resource "aws_lb_target_group" "funding" {
  name        = "encore-tg-funding"
  port        = 18082
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = aws_vpc.main.id

  health_check {
    protocol = "HTTP"
    port     = "18082"
    path     = "/actuator/health"
  }
  tags = { Name = "encore-tg-funding" }
}

# ========== Internal ALB Listener ==========
resource "aws_lb_listener" "internal_http" {
  load_balancer_arn = aws_lb.internal.arn
  port              = 8080
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.funding.arn
  }
}