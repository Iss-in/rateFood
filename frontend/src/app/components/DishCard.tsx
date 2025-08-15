import { Card, CardContent, CardHeader } from "./ui/card";
import { Badge } from "./ui/badge";
import { RatingComponent } from "./RatingComponent";
import { ImageWithFallback } from "./figma/ImageWithFallback";
import { MapPin, DollarSign } from "lucide-react";

export interface Dish {
  id: string;
  name: string;
  restaurant: string;
  description: string;
  // price: number;
  // rating: number;
  tags: string[];
  image: string;
  // location: string;
  // distance: number;
}

interface DishCardProps {
  dish: Dish;
  onRatingChange: (dishId: string, rating: number) => void;
}

export function DishCard({ dish, onRatingChange }: DishCardProps) {
  return (
    <Card className="overflow-hidden hover:shadow-md transition-shadow">
      <CardHeader className="p-0">
        <ImageWithFallback
          src={dish.image}
          alt={dish.name}
          className="w-full h-48 object-cover"
        />
      </CardHeader>
      <CardContent className="p-4">
        <div className="space-y-3">
          <div>
            <h3 className="font-semibold">{dish.name}</h3>
            <p className="text-sm text-muted-foreground">{dish.restaurant}</p>
          </div>
          
          <p className="text-sm text-gray-600 line-clamp-2">{dish.description}</p>
          
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
              <Badge key={tag} variant="secondary" className="text-xs">
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