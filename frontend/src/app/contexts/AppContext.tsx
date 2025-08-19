'use client'
import { createContext, useContext, useState, ReactNode } from 'react';
import { Dish } from "@/app/components/DishCard";
import { Restaurant } from "@/app/components/RestaurantCard";

interface AppContextType {
  selectedCity: string;
  setSelectedCity: (city: string) => void;
  selectedTab: string;
  setSelectedTab: (tab: string) => void;
  handleAddDish: (newDish: Omit<Dish, "id" | "rating" | "favoriteCount">) => void;
  setDishes: (dishes: Dish[]) => void;
  handleAddRestaurant: (newRestaurant: Omit<Restaurant, "id" | "rating">) => void;
  setRestaurants: (restaurants: Restaurant[]) => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export function AppProvider({ children }: { children: ReactNode }) {
  const [selectedCity, setSelectedCity] = useState('');
  const [selectedTab, setSelectedTab] = useState('dishes');
  const [dishes, setDishes] = useState<Dish[]>([]);
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);


  const handleAddDish = (newDish: Omit<Dish, "id" | "rating" | "favoriteCount">) => {
    const dish: Dish = {
      ...newDish,
      id: Date.now().toString(),
      favoriteCount: 0,  // provide default value here
    };
    setDishes(prev => [dish, ...prev]);
  };

  const handleAddRestaurant = (newRestaurant: Omit<Restaurant, "id" | "rating">) => {
    const restaurant: Restaurant = {
      ...newRestaurant,
      id: Date.now().toString(),
      rating: 0
    };
    setRestaurants(prev => [restaurant, ...prev]);
  };

  return (
    <AppContext.Provider value={{
      selectedCity,
      setSelectedCity,
      selectedTab,
      setSelectedTab,
      handleAddDish,
      setDishes,
      handleAddRestaurant,
      setRestaurants
    }}>
      {children}
    </AppContext.Provider>
  );
}

export function useAppContext() {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useAppContext must be used within an AppProvider');
  }
  return context;
}
