# SmartPOS Frontend

Modern, responsive Point of Sale Management System frontend built with Next.js

## Features

- 📱 Fully responsive design (mobile, tablet, desktop)
- 🎨 Modern UI with Tailwind CSS
- 📊 Real-time analytics dashboard
- 🛍️ Product & inventory management
- 💳 Sales & transaction processing
- 👥 Customer management
- 📈 Sales reports and analytics
- 🔄 Returns management
- 🧾 Receipt printing
- ⚡ Fast performance with Next.js

## Getting Started

### Prerequisites

- Node.js 18+ 
- npm or yarn

### Installation

```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build

# Start production server
npm start
```

The application will be available at `http://localhost:3000`

## Project Structure

```
src/
├── pages/              # Next.js pages
├── components/         # Reusable React components
├── lib/               # Utility functions and helpers
├── hooks/             # Custom React hooks
├── store/             # Zustand state management
├── types/             # TypeScript type definitions
└── globals.css        # Global styles
```

## API Integration

The frontend connects to the Spring Boot backend at `http://localhost:8080/api`

### Available APIs

- **Products**: `/v1/products`
- **Suppliers**: `/v1/suppliers`
- **Customers**: `/v1/customers`
- **Tickets**: `/v1/tickets`
- **Close Cash**: `/v1/close-cash`
- **Stock Levels**: `/v1/stock-levels`

## Technologies Used

- **Next.js 14**: React framework
- **TypeScript**: Type-safe development
- **Tailwind CSS**: Utility-first CSS
- **Zustand**: State management
- **Axios**: HTTP client
- **React Hook Form**: Form handling
- **Recharts**: Data visualization
- **React Icons**: Icon library

## Environment Variables

Create a `.env.local` file:

```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```
