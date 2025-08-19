'use client'
import { usePathname } from 'next/navigation';
import { useEffect } from 'react';
import { useAppContext } from "@/app/contexts/AppContext";
import { Navbar } from "@/app/components/Navbar";

export function NavbarWrapper() {
    const pathname = usePathname();
    const { selectedTab, setSelectedTab, selectedCity, setSelectedCity, handleAddDish, handleAddRestaurant } = useAppContext();

    useEffect(() => {
        // Example routing logic to switch tabs
        setSelectedTab(selectedTab);
        // if (pathname.startsWith('/restaurants')) {
        //     setSelectedTab('restaurants');
        // } else {
        //     setSelectedTab('dishes');
        // }
    }, [pathname, setSelectedTab, selectedTab ]);

    return (
        <Navbar
            selectedCity={selectedCity}
            onCityChange={setSelectedCity}
            selectedTab={selectedTab}
            onTabChange={setSelectedTab}
            onAddDish={handleAddDish}
            onAddRestaurant={handleAddRestaurant}
        />
    );
}
