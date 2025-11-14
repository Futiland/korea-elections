import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  reactStrictMode: true,
  async redirects() {
    return [
      {
        source: '/everyone-vote',
        destination: '/everyone-poll',
        permanent: true,
      },
    ];
  },
};

export default nextConfig;
