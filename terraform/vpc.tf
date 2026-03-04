# ========== VPC ==========
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = { Name = "encore-vpc" }
}

# ========== Internet Gateway ==========
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
  tags   = { Name = "encore-igw" }
}

# ========== Subnets ==========
resource "aws_subnet" "public1" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.0.0/20"
  availability_zone = "ap-northeast-2a"
  tags = { Name = "encore-subnet-public1-ap-northeast-2a" }
}

resource "aws_subnet" "public2" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.16.0/20"
  availability_zone = "ap-northeast-2c"
  tags = { Name = "encore-subnet-public2-ap-northeast-2c" }
}

resource "aws_subnet" "private1" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.128.0/20"
  availability_zone = "ap-northeast-2a"
  tags = { Name = "encore-subnet-private1-ap-northeast-2a" }
}

resource "aws_subnet" "private2" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.144.0/20"
  availability_zone = "ap-northeast-2c"
  tags = { Name = "encore-subnet-private2-ap-northeast-2c" }
}

resource "aws_subnet" "private3" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.160.0/20"
  availability_zone = "ap-northeast-2a"
  tags = { Name = "encore-subnet-private3-ap-northeast-2a" }
}

resource "aws_subnet" "private4" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.176.0/20"
  availability_zone = "ap-northeast-2c"
  tags = { Name = "encore-subnet-private4-ap-northeast-2c" }
}

# ========== Route Tables ==========
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }
  tags = { Name = "encore-rtb-public" }
}

resource "aws_route_table_association" "public1" {
  subnet_id      = aws_subnet.public1.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "public2" {
  subnet_id      = aws_subnet.public2.id
  route_table_id = aws_route_table.public.id
}

# Private 라우팅 테이블은 각각 독립 (NAT GW 추가 시 route 블록 추가)
resource "aws_route_table" "private1" {
  vpc_id = aws_vpc.main.id
  tags   = { Name = "encore-rtb-private1-ap-northeast-2a" }
}

resource "aws_route_table" "private2" {
  vpc_id = aws_vpc.main.id
  tags   = { Name = "encore-rtb-private2-ap-northeast-2c" }
}

resource "aws_route_table" "private3" {
  vpc_id = aws_vpc.main.id
  tags   = { Name = "encore-rtb-private3-ap-northeast-2a" }
}

resource "aws_route_table" "private4" {
  vpc_id = aws_vpc.main.id
  tags   = { Name = "encore-rtb-private4-ap-northeast-2c" }
}

resource "aws_route_table_association" "private1" {
  subnet_id      = aws_subnet.private1.id
  route_table_id = aws_route_table.private1.id
}

resource "aws_route_table_association" "private2" {
  subnet_id      = aws_subnet.private2.id
  route_table_id = aws_route_table.private2.id
}

resource "aws_route_table_association" "private3" {
  subnet_id      = aws_subnet.private3.id
  route_table_id = aws_route_table.private3.id
}

resource "aws_route_table_association" "private4" {
  subnet_id      = aws_subnet.private4.id
  route_table_id = aws_route_table.private4.id
}