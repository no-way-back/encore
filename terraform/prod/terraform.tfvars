aws_region = "ap-northeast-2"

vpc_id = "vpc-0d00f4ed5d024a1dd"

private_subnet_ids = [
  "subnet-051a77d4ba264f41c",  # 2a
  "subnet-0b737ca5193fe795a"   # 2c
]

rds_security_group_id = "sg-0a11dc3fec3b8063e"

ecs_task_security_group_id = "sg-098cda9cede1475c0"
bastion_security_group_id  = "sg-02c43c5bbc4bb4b4f"

kafka_bootstrap_servers = "boot-kjsx6tll.c2.kafka-serverless.ap-northeast-2.amazonaws.com:9098"