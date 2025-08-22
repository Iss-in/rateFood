// next.config.js
/** @type {import('next').NextConfig} */
const nextConfig = {
  images: {
    remotePatterns: [
      // {
      //   protocol: 'http',
      //   hostname: 'minio',
      //   port: '9001',
      //   pathname: '/**',
      // },
    ],
    unoptimized: true,
  },
}

module.exports = nextConfig