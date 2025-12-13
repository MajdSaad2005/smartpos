import React from 'react';
import type { AppProps } from 'next/app';
import { Toaster } from 'react-hot-toast';
import '@/globals.css';

function App({ Component, pageProps }: AppProps) {
  return (
    <>
      <Component {...pageProps} />
      <Toaster position="bottom-right" />
    </>
  );
}

export default App;
