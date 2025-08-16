# Stage 1: Build the Next.js app
FROM node:20-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package.json and lock files
COPY package.json package-lock.json* ./

# Install dependencies
RUN npm install --frozen-lockfile

# Copy the source code
COPY . .

# Build the Next.js app
RUN npm run build

# Stage 2: Run the production app
FROM node:20-alpine AS runner
WORKDIR /app

# Set NODE_ENV for production
ENV NODE_ENV=production

# Copy built assets and needed files from builder
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

# Next.js runs on 3000 by default
EXPOSE 3000

# Start Next.js
CMD ["npm", "start"]
