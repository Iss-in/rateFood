import { MapPin } from "lucide-react";
import React, {useState, useEffect, useMemo, useRef} from "react";
import { FixedSizeList as List, ListChildComponentProps } from "react-window";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "../components/ui/tabs";
import { AddDishDialog } from "./AddDishDialog";
import { AddRestaurantDialog } from "./AddRestaurantDialog";
import {Dish} from "@/app/components/DishCard";
import {Restaurant} from "@/app/components/RestaurantCard";


interface NavbarProps {
  selectedCity: string;
  onCityChange: (city: string) => void;
  selectedTab: string;
  onTabChange: (tab: string) => void;
  onAddDish: (newDish: Omit<Dish, "id" | "rating">) => void;
  onAddRestaurant: (newRestaurant: Omit<Restaurant, "id" | "rating">) => void;
}

export function Navbar({ selectedCity, onCityChange, selectedTab, onTabChange, onAddDish, onAddRestaurant  }: NavbarProps) {
    const [cities, setCities] = useState<string[]>([]);
    const [citySearch, setCitySearch] = useState("");
    const [filteredCities, setFilteredCities] = useState<string[]>([]);
    const [cityPage, setCityPage] = useState(0);
    const [cityTotalPages, setCityTotalPages] = useState(1);
    const [loading, setLoading] = useState(false);
    const [hasMore, setHasMore] = useState(true); // indicates if more cities available
    const [highlightedIndex, setHighlightedIndex] = useState(0);
    const debounceRef = useRef<NodeJS.Timeout | null>(null);
    const PAGE_SIZE = 20; // or adjust per backend


    const [isCityModalOpen, setIsCityModalOpen] = useState(false);
    const searchInputRef = useRef<HTMLInputElement>(null);
    const modalRef = useRef<HTMLDivElement>(null); // for outside click detection
    const listRef = useRef<List>(null);

    const resultListRef = useRef<HTMLDivElement>(null);

    // keep updating window height
    const [listHeight, setListHeight] = useState(0);
    useEffect(() => {
        function updateHeight() {
            setListHeight(Math.round(window.innerHeight * 0.30)); // 40vh in px
        }

        updateHeight(); // Set initial height on mount

        window.addEventListener('resize', updateHeight);

        return () => window.removeEventListener('resize', updateHeight);
    }, []);


    useEffect(() => {
        if (selectedCity) {
            localStorage.setItem('selectedCity', selectedCity);
        }
    }, [selectedCity]);


    // useEffect(() => {
    // fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/city`) // your backend endpoint URL here
    //     .then((res) => {
    //       if (!res.ok) throw new Error("Failed to fetch cities");
    //       return res.json();
    //     })
    //     .then((data) => {
    //       // console.log(data);
    //       setCities(data);
    //       setLoading(false);
    //       // onCityChange(data[0]);
    //     })
    //     .catch((err) => {
    //       console.error(err);
    //       setLoading(false);
    //     });
    // }, []);
    useEffect(() => {
        if (!isCityModalOpen) return;
        if (debounceRef.current) clearTimeout(debounceRef.current);
        debounceRef.current = setTimeout(() => {
            setLoading(true);
            fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/api/city?name=${encodeURIComponent(citySearch)}&page=0&size=${PAGE_SIZE}`
            )
                .then(res => {
                    if (!res.ok) throw new Error("Failed to fetch cities");
                    return res.json();
                })
                .then(data => {
                    setFilteredCities(data.data);
                    setCityTotalPages(data.totalPages);
                    setHasMore(cityTotalPages > 1); // optional, can still track if wanted
                })
                .catch(() => {
                    setFilteredCities([]);
                    setCityTotalPages(1);
                    setHasMore(false);
                })
                .finally(() => setLoading(false));
        }, 300);
        return () => {
            if (debounceRef.current) clearTimeout(debounceRef.current);
        };
    }, [citySearch, isCityModalOpen]);




    // autqoficus search bar
    useEffect(() => {
        if (isCityModalOpen && searchInputRef.current) {
            searchInputRef.current.focus();
        }
        if (isCityModalOpen) {
            setCitySearch(""); // clear field each time modal opens
        }
    }, [isCityModalOpen]);

    // Close on Escape key
    useEffect(() => {
        const handleEsc = (e: KeyboardEvent) => {
            if (e.key === "Escape") {
                setIsCityModalOpen(false);
            }
        };
        if (isCityModalOpen) {
            document.addEventListener("keydown", handleEsc);
        }
        return () => {
            document.removeEventListener("keydown", handleEsc);
        };
    }, [isCityModalOpen]);

    // highlight items in search
    useEffect(() => {
        if (isCityModalOpen) {
            setHighlightedIndex(0);
        }
    }, [isCityModalOpen, citySearch]);

    useEffect(() => {
        if (listRef.current) {
            listRef.current.scrollToItem(highlightedIndex);
        }
    }, [highlightedIndex]);


    // Handle keyboard navigation for above
    const handleKeyDown = (e: React.KeyboardEvent) => {
        if (!filteredCities.length) return;

        if (e.key === "ArrowDown") {
            e.preventDefault();
            setHighlightedIndex((prev) => Math.min(prev + 1, filteredCities.length - 1));
        } else if (e.key === "ArrowUp") {
            e.preventDefault();
            setHighlightedIndex((prev) => Math.max(prev - 1, 0));
        } else if (e.key === "Enter") {
            e.preventDefault();
            if (filteredCities[highlightedIndex]) {
                onCityChange(filteredCities[highlightedIndex]);
                setIsCityModalOpen(false);
                setCitySearch("");
            }
        }
    };




    // Close on click outside
    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            if (modalRef.current && !modalRef.current.contains(e.target as Node)) {
                setIsCityModalOpen(false);
            }
        };
        if (isCityModalOpen) {
            document.addEventListener("mousedown", handleClickOutside);
        }
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [isCityModalOpen]);



    // // // Filter functions for cities
    // const filteredCities = useMemo(() => {
    // return cities.filter(city => {
    //   const matchesSearch = city.toLowerCase().includes(citySearch.toLowerCase())
    //
    //   return matchesSearch
    // });
    // }, [cities, citySearch]);


    const Row = ({ index, style }: ListChildComponentProps) => {
        const city = filteredCities[index];
        const isHighlighted = index === highlightedIndex;
        return (
            <button
                style={style}
                key={city}
                onClick={() => {
                    onCityChange(city);
                    setIsCityModalOpen(false);
                    setCitySearch(""); // Optionally clear search on selection
                }}
                onMouseEnter={() => setHighlightedIndex(index)}
                className={`px-3 py-2 text-left w-full cursor-pointer ${
                    isHighlighted ? "bg-blue-100 rounded" : "hover:bg-gray-100"
                }`}>

                {city}
            </button>
        );
    }
    // const LIST_HEIGHT = 300; // px, adjust as needed
    const LIST_HEIGHT = listHeight; // 40vh in px
    const ITEM_HEIGHT = 40;  // px, height of each city button


    const [addDishOpen, setAddDishOpen] = useState(false);
    const [addRestaurantOpen, setAddRestaurantOpen] = useState(false);


    return (
        <>
            {/* Navbar */}
            <nav className="border-b bg-background sticky top-0 z-50">
                <div className="mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-16">
                        {/* Left: Brand - Always at extreme left */}
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-10 bg-gradient-to-br from-orange-400 to-red-500 rounded-xl flex items-center justify-center">
                                <span className="text-white text-xl">🍽️</span>
                            </div>
                            <h1 className="text-2xl font-bold text-primary hidden sm:flex">FoodieDaddie</h1>
                        </div>

                        {/* Center: Tabs */}
                        <div className="flex justify-center">
                            <Tabs value={selectedTab} onValueChange={onTabChange}>
                                <TabsList className="flex space-x-4">
                                    <TabsTrigger value="dishes">Dishes</TabsTrigger>
                                    <TabsTrigger value="restaurants">Restaurants</TabsTrigger>
                                </TabsList>
                            </Tabs>
                        </div>

                        {/* Right: Add buttons + Location - Always at extreme right */}
                        <div className="flex items-center space-x-2">
                            {/* Add Dish Button */}
                            {selectedTab === "dishes" && selectedCity && (
                                <div className="hidden sm:flex min-w-[140px] ml-auto flex justify-end">
                                    <AddDishDialog
                                        onAddDish={onAddDish}
                                        selectedCity={selectedCity}
                                        open={addDishOpen}
                                        onOpenChange={setAddDishOpen}
                                    />
                                </div>
                            )}
                            {selectedTab === "restaurants" && selectedCity && (
                                <div className="hidden sm:flex min-w-[140px] ml-auto flex justify-end">
                                    <AddRestaurantDialog
                                        onAddRestaurant={onAddRestaurant}
                                        open={addRestaurantOpen}
                                        onOpenChange={setAddRestaurantOpen}
                                    />
                                </div>
                            )}
                            <button
                                onClick={() => setIsCityModalOpen(true)}
                                className="flex items-center space-x-2 rounded px-3 py-1 bg-white"
                            >
                                <MapPin className="h-4 w-4 text-foreground" />
                                <span>{selectedCity || "Select city"}</span>
                            </button>
                        </div>
                    </div>
                </div>
            </nav>


            {/* Overlay & Popup */}
            {isCityModalOpen && (
                // Root overlay catches outside clicks
                <div
                    className="fixed inset-0 z-[99999] bg-gray-800/50 backdrop-blur-sm flex items-center justify-center"
                    onClick={() => setIsCityModalOpen(false)}
                >
                    {/* Popup box stops click propagation */}
                    <div
                        className="bg-white rounded-lg shadow-lg w-full max-w-md p-4 flex flex-col h-[40vh]"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <div className="flex justify-between items-center mb-3">
                            <h2 className="text-lg font-semibold">Select City</h2>
                            <button
                                onClick={() => setIsCityModalOpen(false)}
                                className="text-gray-500 hover:text-black"
                            >
                                ✕
                            </button>
                        </div>

                        {/* Search bar */}
                        <input
                            ref={searchInputRef}
                            type="text"
                            placeholder="Search city..."
                            value={citySearch}
                            onChange={(e) => setCitySearch(e.target.value)}
                            onKeyDown={handleKeyDown} // KEYBOARD NAVIGATION
                            className="w-full px-3 py-2 border rounded mb-3"
                        />

                        {/* Fixed height list area */}
                        <div style={{ height: LIST_HEIGHT }}>
                            {filteredCities.length === 0 ? (
                                <div className="flex  h-full items-start justify-start">
                                    <p className="text-sm text-gray-500 px-3">No cities found.</p>
                                </div>
                            ) : (
                                <List
                                    ref={listRef}
                                    height={LIST_HEIGHT}
                                    itemCount={filteredCities.length}
                                    itemSize={ITEM_HEIGHT}
                                    width="100%"
                                >
                                    {Row}
                                </List>
                            )}
                        </div>
                    </div>
                </div>
            )}

        </>
    );



}