'use client'
import { useState, useEffect, useRef } from 'react';

export function useSafeRouter() {
  const [currentPath, setCurrentPath] = useState('');
  const [isReady, setIsReady] = useState(false);

  const patchedHistory = useRef<{
    originalPushState: typeof window.history.pushState;
    originalReplaceState: typeof window.history.replaceState;
  } | false>(false);

  useEffect(() => {
    if (typeof window === 'undefined') return;

    const updatePath = () => {
      Promise.resolve().then(() => {
        setCurrentPath(window.location.pathname);
        setIsReady(true);
      });
    };

    updatePath();

    const handlePopState = () => {
      updatePath();
    };

    if (!patchedHistory.current) {
      // Patch pushState and replaceState only once
      const originalPushState = window.history.pushState;
      const originalReplaceState = window.history.replaceState;

      window.history.pushState = function (...args) {
        originalPushState.apply(window.history, args);
        updatePath();
      };

      window.history.replaceState = function (...args) {
        originalReplaceState.apply(window.history, args);
        updatePath();
      };

      // Save the originals in ref for cleanup
      patchedHistory.current = {
        originalPushState,
        originalReplaceState,
      };
    }

    window.addEventListener('popstate', handlePopState);

    return () => {
      window.removeEventListener('popstate', handlePopState);
      if (patchedHistory.current && typeof patchedHistory.current === 'object') {
        window.history.pushState = patchedHistory.current.originalPushState;
        window.history.replaceState = patchedHistory.current.originalReplaceState;
        patchedHistory.current = false;
      }
    };
  }, []);

  return {
    currentPath,
    isReady,
    isSubmittedPage: currentPath === '/submitted',
  };
}
