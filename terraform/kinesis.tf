resource "aws_kinesis_firehose_delivery_stream" "kinesis_firehose" {

  destination = "extended_s3"
  name = "kns-delivery-event"

  extended_s3_configuration {

    bucket_arn = "${aws_s3_bucket.bucket.arn}"
    role_arn = "${aws_iam_role.firehose_role.arn}"
    prefix = "ingest/year=!{timestamp:yyyy}/month=!{timestamp:MM}/day=!{timestamp:dd}/hour=!{timestamp:HH}/"
    error_output_prefix = "ingest/!{firehose:error-output-type}/year=!{timestamp:yyyy}/month=!{timestamp:MM}/day=!{timestamp:dd}/hour=!{timestamp:HH}/"
  }


}

resource "aws_s3_bucket" "bucket" {
  bucket = "${var.bucket}"
  acl    = "private"
}

resource "aws_iam_role" "firehose_role" {
  name = "firehose_test_role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "firehose.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_policy" "policy" {

  name = "kns-s3-policy"
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
      {
        "Action": [
          "s3:GetObject",
          "s3:ListBucketMultipartUploads",
          "s3:AbortMultipartUpload",
          "s3:ListBucket",
          "s3:GetBucketLocation",
          "s3:PutObject"
        ],
        "Effect": "Allow",
        "Resource":  [
          "arn:aws:s3:::${var.bucket}/*",
          "arn:aws:s3:::${var.bucket}"
        ]
      }
    ]
  }
  EOF
  }


resource "aws_iam_role_policy_attachment" "kns-s3-policy-attach" {
  policy_arn = aws_iam_policy.policy.arn
  role       = aws_iam_role.firehose_role.name
}