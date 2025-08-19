import { Card, CardContent, CardHeader } from "./ui/card";
import { Badge } from "./ui/badge";
import { ImageWithFallback } from "./figma/ImageWithFallback";
import { useSession, SessionContextType } from "../contexts/SessionContext";
import {Heart, Pencil} from "lucide-react";
import { useState } from "react";
import toast from "react-hot-toast";

export interface Dish {
  id: string;
  name: string;
  restaurant: string;
  description: string;
  tags: string[];
  image: string;
  isFavourite?: boolean;
  favoriteCount: number
}

interface DishCardProps {
  dish: Dish;
  onRemove: () => void;
}

export function DishCard({ dish, onRemove }: DishCardProps) {
  const { session }: SessionContextType = useSession();
  const [isFavourite, setIsFavourite] = useState(dish.isFavourite ?? false);
  const [favoriteCount, setFavoriteCount] = useState(dish.favoriteCount);

  const handlePencilClick = async () => {
    toast.error("clicked on pencil");
    return;
  }

  const handleFavouriteToggle = async () => {
    if (!session.token) {
      toast.error("You must be logged in to favourite a dish.");
      return;
    }




    const endpoint = isFavourite ? "unFavourite" : "favourite";
    const url = `${process.env.NEXT_PUBLIC_API_URL}/foodapp/dish/${endpoint}/${dish.id}`;

    try {
      const response = await fetch(url, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${session.token}`,
        },
      });

      if (response.ok) {
        if (isFavourite) {
          // Currently favourited → unfavourite
          setIsFavourite(false);
          setFavoriteCount((prev) => Math.max(0, prev - 1));
          onRemove(); // remove from parent if needed
          toast.success(t => (
              <div onClick={() => toast.dismiss(t.id)} style={{ cursor: "pointer" }}>
                Dish unfavourited successfully!
              </div>
          ));

        } else {
          // Currently not favourited → favourite
          setIsFavourite(true);
          setFavoriteCount((prev) => prev + 1);
          toast.success(t => (
              <div onClick={() => toast.dismiss(t.id)} style={{ cursor: "pointer" }}>
                Dish Favourited successfully!
              </div>
          ));

        }
      } else {
        const errorData = await response.json();
        toast.error(errorData.message || "Failed to update favourite status.");
      }
    } catch (error) {
      toast.error(`${error} error occurred. Please try again.`);
    }
  };

  return (
    <Card className="overflow-hidden hover:shadow-md shadow-md transition-shadow">
      <CardHeader className="p-0">
        <div className="relative w-full h-48">
          <ImageWithFallback
            src={dish.image}
            alt={dish.name}
            className="object-cover"
          />
          {session.isLoggedIn && (
              <>
                <div
                    className="absolute top-2 right-2 bg-white rounded-full p-1.5 cursor-pointer flex flex-col items-center"
                    onClick={handleFavouriteToggle}
                >
                  <Heart
                      className={`w-6 h-6 ${
                          isFavourite ? "text-red-500 fill-current" : "text-gray-500"
                      }`}
                  />
                  <span className="text-sm font-medium text-gray-700">
                    {favoriteCount}
                  </span>
                </div>


              <div
                className="absolute top-2 left-2 bg-white rounded-full p-1.5 cursor-pointer"
                onClick={handlePencilClick}  >
                <Pencil className="w-6 h-6 text-gray-700 hover:text-gray-900" />
              </div>
              </>
          )}
        </div>
      </CardHeader>
      <CardContent className="p-4">
        <div className="space-y-3">
          <div>
            <h3 className="font-semibold">{dish.name}</h3>
            <p className="text-sm text-muted-foreground">{dish.restaurant}</p>
          </div>

          <p className="text-sm text-gray-600 line-clamp-2">
            {dish.description}
          </p>

          <div className="flex items-center justify-between">
            {/*<div className="flex items-center space-x-1">*/}
            {/*  <DollarSign className="h-4 w-4 text-green-600" />*/}
            {/*  <span className="font-medium">${dish.price}</span>*/}
            {/*</div>*/}
            {/*<div className="flex items-center space-x-1 text-sm text-muted-foreground">*/}
            {/*  <MapPin className="h-3 w-3" />*/}
            {/*  <span>{dish.distance} mi</span>*/}
            {/*</div>*/}
          </div>

          <div className="flex flex-wrap gap-1">
            {dish.tags.slice(0, 3).map((tag) => (
              <Badge key={tag} className="text-xs">
                {tag}
              </Badge>
            ))}
          </div>

          {/*<div className="pt-2">*/}
          {/*  <RatingComponent*/}
          {/*    currentRating={dish.rating}*/}
          {/*    onRatingChange={(rating) => onRatingChange(dish.id, rating)}*/}
          {/*  />*/}
          {/*</div>*/}
        </div>
      </CardContent>
    </Card>
  );
}
